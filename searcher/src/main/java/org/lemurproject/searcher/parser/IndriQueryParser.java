package org.lemurproject.searcher.parser;

import java.io.IOException;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.analysis.TokenStream;
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.BoostQuery;
import org.apache.lucene.search.IndriTermQueryWrapper;
import org.apache.lucene.search.Query;
import org.lemurproject.searcher.IndriAndQuery;
import org.lemurproject.searcher.IndriBandQuery;
import org.lemurproject.searcher.IndriMaxQuery;
import org.lemurproject.searcher.IndriNearQuery;
import org.lemurproject.searcher.IndriOrQuery;
import org.lemurproject.searcher.IndriWeightedSumQuery;
import org.lemurproject.searcher.IndriWindowQuery;
import org.lemurproject.searcher.domain.QueryParserOperatorQuery;
import org.lemurproject.searcher.domain.QueryParserQuery;
import org.lemurproject.searcher.domain.QueryParserTermQuery;
import org.lemurproject.sifaka.luceneanalyzer.EnglishAnalyzerConfigurable;

public class IndriQueryParser {

	private final static String AND = "and";
	private final static String BAND = "band";
	private final static String NEAR = "near";
	private final static String OR = "or";
	private final static String WAND = "wand";
	private final static String WEIGHT = "weight";
	private final static String WINDOW = "window";
	private final static String UNORDER_WINDOW = "uw";
	private final static String WSUM = "wsum";
	private final static String MAX = "max";
	private final static String COMBINE = "combine";
	private final static String SCOREIF = "scoreif";

	private final Analyzer analyzer;

	public IndriQueryParser() {
		analyzer = getConfigurableAnalyzer();
	}

	private Analyzer getConfigurableAnalyzer() {
		EnglishAnalyzerConfigurable an = new EnglishAnalyzerConfigurable();
		an.setLowercase(true);
		an.setStopwordRemoval(true);
		an.setStemmer(EnglishAnalyzerConfigurable.StemmerType.KSTEM);
		return an;
	}

	/**
	 * Count the number of occurrences of character c in string s.
	 * 
	 * @param c A character.
	 * @param s A string.
	 */
	private static int countChars(String s, char c) {
		int count = 0;

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == c) {
				count++;
			}
		}
		return count;
	}

	/**
	 * Get the index of the right parenenthesis that balances the left-most
	 * parenthesis. Return -1 if it doesn't exist.
	 * 
	 * @param s A string containing a query.
	 */
	private static int indexOfBalencingParen(String s) {
		int depth = 0;

		for (int i = 0; i < s.length(); i++) {
			if (s.charAt(i) == '(') {
				depth++;
			} else if (s.charAt(i) == ')') {
				depth--;

				if (depth == 0) {
					return i;
				}
			}
		}
		return -1;
	}

	private QueryParserOperatorQuery createOperator(String operatorName) {
		QueryParserOperatorQuery operatorQuery = new QueryParserOperatorQuery();

		int operatorDistance = 0;
		String operatorNameLowerCase = (new String(operatorName)).toLowerCase();
		operatorNameLowerCase = operatorNameLowerCase.replace("#", "");

		// Translate indri syntax for near and unordered window
		if (operatorNameLowerCase.matches("\\d+")) {
			operatorNameLowerCase = String.join("/", NEAR, operatorNameLowerCase);
		} else if (operatorNameLowerCase.startsWith(UNORDER_WINDOW)) {
			String[] parts = operatorNameLowerCase.split(UNORDER_WINDOW);
			operatorNameLowerCase = String.join("/", WINDOW, parts[1]);
		}

		// Remove the distance argument to proximity operators.
		if (operatorNameLowerCase.startsWith(NEAR) || operatorNameLowerCase.startsWith(WINDOW)) {
			String[] substrings = operatorNameLowerCase.split("/", 2);

			if (substrings.length < 2) {
				syntaxError("Missing distance argument for #near or #window");
			}

			operatorNameLowerCase = substrings[0];
			operatorDistance = Integer.parseInt(substrings[1]);
		}
		operatorQuery.setOperator(operatorNameLowerCase);
		operatorQuery.setField("fulltext");
		operatorQuery.setDistance(operatorDistance);

		return operatorQuery;
	}

	private class PopWeight {
		private Float weight;
		private String queryString;

		public Float getWeight() {
			return weight;
		}

		public void setWeight(Float weight) {
			this.weight = weight;
		}

		public String getQueryString() {
			return queryString;
		}

		public void setQueryString(String queryString) {
			this.queryString = queryString;
		}
	}

	/**
	 * Remove a weight from an argument string. Return the weight and the modified
	 * argument string.
	 * 
	 * @param String A partial query argument string, e.g., "3.0 fu 2.0 bar".
	 * @return PopData<String,String> The weight string and the modified argString
	 *         (e.g., "3.0" and "fu 2.0 bar".
	 */
	private PopWeight popWeight(String argString, Float weight) {

		String[] substrings = argString.split("[ \t]+", 2);

		if (substrings.length < 2) {
			syntaxError("Missing weight or query argument");
		}

		PopWeight popWeight = new PopWeight();
		popWeight.setWeight(Float.valueOf(substrings[0]));
		popWeight.setQueryString(substrings[1]);

		return popWeight;
	}

	/**
	 * Remove a subQuery from an argument string. Return the subquery and the
	 * modified argument string.
	 * 
	 * @param String A partial query argument string, e.g., "#and(a b) c d".
	 * @return PopData<String,String> The subquery string and the modified argString
	 *         (e.g., "#and(a b)" and "c d".
	 */
	private String popSubquery(String argString, QueryParserOperatorQuery queryTree, Float weight) {

		int i = indexOfBalencingParen(argString);

		if (i < 0) { // Query syntax error. The parser
			i = argString.length(); // handles it. Here, just don't fail.
		}

		String subquery = argString.substring(0, i + 1);
		queryTree.addSubquery(parseQueryString(subquery), weight);

		argString = argString.substring(i + 1);

		return argString;
	}

	/**
	 * Remove a term from an argument string. Return the term and the modified
	 * argument string.
	 * 
	 * @param String A partial query argument string, e.g., "a b c d".
	 * @return PopData<String,String> The term string and the modified argString
	 *         (e.g., "a" and "b c d".
	 */
	private String popTerm(String argString, QueryParserOperatorQuery queryTree, Float weight) {
		String[] substrings = argString.split("[ \t\n\r]+", 2);
		String token = substrings[0];

		// Split the token into a term and a field.
		int delimiter = token.indexOf('.');
		String field = null;
		String term = null;

		if (delimiter < 0) { // .fulltext is the default field
			field = "fulltext";
			term = token;
		} else { // Remove the field from the token
			field = token.substring(delimiter + 1).toLowerCase();
			term = token.substring(0, delimiter);
		}

		List<String> tokens = tokenizeString(analyzer, term);
		for (String t : tokens) {
			// Creat the term query
			QueryParserTermQuery termQuery = new QueryParserTermQuery();
			termQuery.setTerm(t);
			termQuery.setField(field);
			queryTree.addSubquery(termQuery, weight);
		}

		if (substrings.length < 2) { // Is this the last argument?
			argString = "";
		} else {
			argString = substrings[1];
		}

		return argString;
	}

	private QueryParserQuery parseQueryString(String queryString) {
		// Create the query tree
		// This simple parser is sensitive to parenthensis placement, so
		// check for basic errors first.
		queryString = queryString.trim(); // The last character should be ')'

		if ((countChars(queryString, '(') == 0) || (countChars(queryString, '(') != countChars(queryString, ')'))
				|| (indexOfBalencingParen(queryString) != (queryString.length() - 1))) {
			// throw IllegalArgumentException("Missing, unbalanced, or misplaced
			// parentheses");
		}

		// The query language is prefix-oriented, so the query string can
		// be processed left to right. At each step, a substring is
		// popped from the head (left) of the string, and is converted to
		// a Qry object that is added to the query tree. Subqueries are
		// handled via recursion.

		// Find the left-most query operator and start the query tree.
		String[] substrings = queryString.split("[(]", 2);
		String queryOperator = AND;
		if (substrings.length > 1) {
			queryOperator = substrings[0].trim();
		}
		QueryParserOperatorQuery queryTree = createOperator(queryOperator);

		// Start consuming queryString by removing the query operator and
		// its terminating ')'. queryString is always the part of the
		// query that hasn't been processed yet.

		if (substrings.length > 1) {
			queryString = substrings[1];
			queryString = queryString.substring(0, queryString.lastIndexOf(")")).trim();
		}

		// Each pass below handles one argument to the query operator.
		// Note: An argument can be a token that produces multiple terms
		// (e.g., "near-death") or a subquery (e.g., "#and (a b c)").
		// Recurse on subqueries.

		while (queryString.length() > 0) {

			// If the operator uses weighted query arguments, each pass of
			// this loop must handle "weight arg". Handle the weight first.

			Float weight = null;
			if ((queryTree.getOperator().equals(WEIGHT)) || (queryTree.getOperator().equals(WAND))
					|| queryTree.getOperator().equals(WSUM)) {
				PopWeight popWeight = popWeight(queryString, weight);
				weight = popWeight.getWeight();
				queryString = popWeight.getQueryString();
			}

			// Now handle the argument (which could be a subquery).
			if (queryString.charAt(0) == '#') { // Subquery
				queryString = popSubquery(queryString, queryTree, weight).trim();
			} else { // Term
				queryString = popTerm(queryString, queryTree, weight);
			}
		}

		return queryTree;
	}

	public Query parseQuery(String queryString) {
		// TODO: json or indri query
		QueryParserQuery qry = parseQueryString(queryString);
		return getLuceneQuery(qry);
	}

	public Query parseJsonQueryString(String jsonQueryString) {
		// TODO: json implementation
		return null;
	}

	private Query getLuceneQuery(QueryParserQuery queryTree) {
		BooleanClause clause = createBooleanClause(queryTree);
		Query query = clause.getQuery();
		return query;
	}

	public BooleanClause createBooleanClause(QueryParserQuery queryTree) {
		Query query = null;
		if (queryTree instanceof QueryParserOperatorQuery) {
			QueryParserOperatorQuery operatorQuery = (QueryParserOperatorQuery) queryTree;

			// Create clauses for subqueries
			List<BooleanClause> clauses = new ArrayList<>();
			for (QueryParserQuery subquery : operatorQuery.getSubqueries()) {
				BooleanClause clause = createBooleanClause(subquery);
				clauses.add(clause);
			}

			// Create Operator
			if (operatorQuery.getOperator().equalsIgnoreCase(OR)) {
				query = new IndriOrQuery(clauses);
			} else if (operatorQuery.getOperator().equalsIgnoreCase(WSUM)) {
				query = new IndriWeightedSumQuery(clauses);
			} else if (operatorQuery.getOperator().equalsIgnoreCase(MAX)) {
				query = new IndriMaxQuery(clauses);
			} else if (operatorQuery.getOperator().equalsIgnoreCase(WAND)) {
				query = new IndriAndQuery(clauses);
			} else if (operatorQuery.getOperator().equalsIgnoreCase(NEAR)) {
				query = new IndriNearQuery(clauses, operatorQuery.getField(), operatorQuery.getDistance());
			} else if (operatorQuery.getOperator().equalsIgnoreCase(WINDOW)) {
				query = new IndriWindowQuery(clauses, operatorQuery.getField(), operatorQuery.getDistance());
			} else if (operatorQuery.getOperator().equalsIgnoreCase(BAND)) {
				query = new IndriBandQuery(clauses);
			} else {
				query = new IndriAndQuery(clauses);
			}
		} else if (queryTree instanceof QueryParserTermQuery) {
			// Create term query
			QueryParserTermQuery termQuery = (QueryParserTermQuery) queryTree;
			// System.out.println(jsonQuery);
			String field = "all";
			if (termQuery.getField() != null) {
				field = termQuery.getField();
			}
			query = new IndriTermQueryWrapper(new Term(field, termQuery.getTerm()));
		}
		if (queryTree.getBoost() != null) {
			query = new BoostQuery(query, queryTree.getBoost().floatValue());
		}
		BooleanClause clause = new BooleanClause(query, Occur.SHOULD);
		return clause;
	}

	/**
	 * Given part of a query string, returns an array of terms with stopwords
	 * removed and the terms stemmed using the Krovetz stemmer. Use this method to
	 * process raw query terms.
	 * 
	 * @param query String containing query.
	 * @return Array of query tokens
	 * @throws IOException Error accessing the Lucene index.
	 */
	public static List<String> tokenizeString(Analyzer analyzer, String string) {
		List<String> tokens = new ArrayList<>();
		try (TokenStream tokenStream = analyzer.tokenStream(null, new StringReader(string))) {
			tokenStream.reset(); // required
			while (tokenStream.incrementToken()) {
				tokens.add(tokenStream.getAttribute(CharTermAttribute.class).toString());
			}
		} catch (IOException e) {
			new RuntimeException(e); // Shouldn't happen...
		}
		return tokens;
	}

	/**
	 * Throw an error specialized for query parsing syntax errors.
	 * 
	 * @param errorString The string "Syntax
	 * @throws IllegalArgumentException The query contained a syntax error
	 */
	static private void syntaxError(String errorString) throws IllegalArgumentException {
		throw new IllegalArgumentException("Syntax Error: " + errorString);
	}

}
