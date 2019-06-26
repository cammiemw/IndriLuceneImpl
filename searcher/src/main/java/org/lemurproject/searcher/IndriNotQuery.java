package org.lemurproject.searcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.IndriNotWeight;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Weight;

public class IndriNotQuery extends Query implements Iterable<BooleanClause> {
	private List<BooleanClause> clauses; // used for toString() and getClauses()

	public IndriNotQuery(List<BooleanClause> clauses) {
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
	public Weight createWeight(IndexSearcher searcher, ScoreMode scoreModes, float boost) throws IOException {
		IndriNotQuery query = this;
		return new IndriNotWeight(query, searcher, scoreModes, boost);
	}

	@Override
	public Iterator<BooleanClause> iterator() {
		return clauses.iterator();
	}

}
