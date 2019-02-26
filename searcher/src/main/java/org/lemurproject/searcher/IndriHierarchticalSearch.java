package org.lemurproject.searcher;

import java.io.BufferedWriter;
import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.index.Term;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanClause.Occur;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.IndriTermQueryWrapper;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.lemurproject.sifaka.luceneanalyzer.EnglishAnalyzerConfigurable;

import com.google.gson.Gson;

public class IndriHierarchticalSearch {

	private final static String EXTERNALID_FIELD = "externalId";
	private final static String ID_FIELD = "internalId";
	private final static String DATE_FIELD = "date";
	private final static String SUBJECT_FIELD = "subject";
	private final static String TITLE_FIELD = "title";
	private final static String BODY_FIELD = "body";

	private final static String PARSING_FIELD = "body";

	public static void main(String[] args) throws IOException, ParseException {
		String indexDir = "C://dev//Indexes_Lucene_1.0//toyNoStopwords";
		Gson gson = new Gson();

		Directory dir = FSDirectory.open(Paths.get(indexDir));
		IndexReader reader = DirectoryReader.open(dir);
		IndexSearcher searcher = new IndexSearcher(reader);

		Similarity indriDirichlet = new IndriDirichletSimilarity();
		searcher.setSimilarity(indriDirichlet);

//	Similarity LmJm = new IndriJelinekMercerSimilarity(.4f);
//	searcher.setSimilarity(LmJm);

		Analyzer analyzer = getConfigurableAnalyzer();
		QueryParser qp = new QueryParser(PARSING_FIELD, analyzer);

		Path path = Paths.get("toyResults.txt");
		BufferedWriter writer = java.nio.file.Files.newBufferedWriter(path);

		Scanner scanner = new Scanner(new File("toyQueries.json"));

		while (scanner.hasNextLine()) {
			String queryLine = scanner.nextLine();
			JsonQueryObject queryObject = gson.fromJson(queryLine, JsonQueryObject.class);
			String queryNum = queryObject.getQid();
			String queryString = queryObject.getQuery();

			queryString = queryString.replaceAll("-", " ");
			// queryString = queryString.replaceAll("[^a-zA-Z0-9\\s]", "");
			String stemmedString = qp.parse(queryString).toString();
			String[] queryTermArray = stemmedString.split(" ");

			List<BooleanClause> clauses = new ArrayList<>();
//			for (String queryTerm : queryTermArray) {
//				String[] queryParts = queryTerm.split(":");
//				Query innerQuery = null;
//				if (queryParts[1].contains("^")) {
//					String[] boostSplit = queryParts[1].split("\\^");
//					float boost = Float.valueOf(boostSplit[1]).floatValue();
//					String boostQueryString = boostSplit[0].replaceAll("[^a-zA-Z0-9\\s]", "");
//					innerQuery = new BoostQuery(new IndriTermQueryWrapper(new Term(PARSING_FIELD, boostQueryString)),
//							boost);
//				} else {
//					innerQuery = new IndriTermQueryWrapper(new Term(PARSING_FIELD, queryParts[1]));
//				}
//				clauses.add(new BooleanClause(innerQuery, Occur.SHOULD));
//			}

			Query presidentQuery = new IndriTermQueryWrapper(new Term(PARSING_FIELD, "president"));
			clauses.add(new BooleanClause(presidentQuery, Occur.SHOULD));

			List<BooleanClause> presidentClauses = new ArrayList<>();
			Query trumpQuery = new IndriTermQueryWrapper(new Term(PARSING_FIELD, "trump"));
			presidentClauses.add(new BooleanClause(trumpQuery, Occur.SHOULD));
			Query obamaQuery = new IndriTermQueryWrapper(new Term(PARSING_FIELD, "obama"));
			presidentClauses.add(new BooleanClause(obamaQuery, Occur.SHOULD));
			IndriOrQuery trumpOrObamaQuery = new IndriOrQuery(presidentClauses);
			clauses.add(new BooleanClause(trumpOrObamaQuery, Occur.SHOULD));

			Query test = new IndriQuery(clauses);

			TopDocs hitDocs = searcher.search(test, 100);
			// TopDocs hitDocs = searcher.search(trumpOrObamaQuery, 100);
			ScoreDoc[] scoreDocs = hitDocs.scoreDocs;

			int rank = 0;
			for (ScoreDoc scoreDoc : scoreDocs) {
				rank++;
				int docid = scoreDoc.doc;

				Document doc = searcher.doc(docid);
				String fileName = doc.get(EXTERNALID_FIELD);

				System.out.println(String.join(" ", queryNum, "Q0", fileName, String.valueOf(rank),
						String.valueOf(scoreDoc.score), "lucene"));
				writer.write(String.join(" ", queryNum, "Q0", fileName, String.valueOf(rank),
						String.valueOf(scoreDoc.score), "lucene\n"));
			}
		}
		scanner.close();
		writer.close();

	}

	public static Analyzer getConfigurableAnalyzer() {
		EnglishAnalyzerConfigurable an = new EnglishAnalyzerConfigurable();
		an.setLowercase(true);
		an.setStopwordRemoval(true);
		an.setStemmer(EnglishAnalyzerConfigurable.StemmerType.KSTEM);
		return an;
	}

}
