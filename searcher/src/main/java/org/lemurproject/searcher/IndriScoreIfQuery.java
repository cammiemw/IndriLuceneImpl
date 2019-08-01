package org.lemurproject.searcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.IndriAndWeight;
import org.apache.lucene.search.IndriScoreIfNotWeight;
import org.apache.lucene.search.IndriScoreIfWeight;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Weight;
import org.apache.lucene.search.BooleanClause.Occur;

public class IndriScoreIfQuery extends Query implements Iterable<BooleanClause> {
	private List<BooleanClause> clauses; // used for toString() and getClauses()

	public IndriScoreIfQuery(List<BooleanClause> clauses) {
		this.clauses = clauses;
	}

	@Override
	public String toString(String field) {
		// TODO Auto-generated method stub
		return null;
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
		Query query = this;
		Query required = null;
		for (BooleanClause c : clauses) {
			if (c.getOccur() == Occur.MUST) {
				required = c.getQuery();
			} else {
				query = c.getQuery();
			}
		}
		Weight weight = new IndriScoreIfWeight(required, query, searcher, scoreMode, boost);
		return weight;
		//return new IndriAndWeight(query, searcher, scoreMode, boost);
	}

	@Override
	public Iterator<BooleanClause> iterator() {
		return clauses.iterator();
	}

}
