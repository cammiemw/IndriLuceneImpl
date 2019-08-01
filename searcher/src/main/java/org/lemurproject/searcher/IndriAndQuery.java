package org.lemurproject.searcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.BooleanQuery;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.IndriAndWeight;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Weight;

public class IndriAndQuery extends IndriQuery {
	private List<BooleanClause> clauses; // used for toString() and getClauses()

	public IndriAndQuery(List<BooleanClause> clauses) {
		super(clauses);
		this.clauses = clauses;
	}

	@Override
	public String toString(String field) {
		StringBuilder buffer = new StringBuilder();

		int i = 0;
		for (BooleanClause c : this) {
			buffer.append(c.getOccur().toString());

			Query subQuery = c.getQuery();
			if (subQuery instanceof BooleanQuery) { // wrap sub-bools in parens
				buffer.append("(");
				buffer.append(subQuery.toString(field));
				buffer.append(")");
			} else {
				buffer.append(subQuery.toString(field));
			}

			if (i != clauses.size() - 1) {
				buffer.append(" ");
			}
			i += 1;
		}

		return buffer.toString();
	}

	@Override
	public boolean equals(Object obj) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public int hashCode() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public Weight createWeight(IndexSearcher searcher, ScoreMode scoreMode, float boost) throws IOException {
		IndriAndQuery query = this;
		return new IndriAndWeight(query, searcher, scoreMode, boost);
	}

	@Override
	public Iterator<BooleanClause> iterator() {
		return clauses.iterator();
	}

}
