package org.lemurproject.searcher;

import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Weight;

public abstract class IndriProximityQuery extends Query implements Iterable<BooleanClause> {

	private final String field;
	private List<BooleanClause> clauses;

	public IndriProximityQuery(List<BooleanClause> clauses, String field) {
		this.clauses = clauses;
		this.field = field;
	}

	@Override
	public abstract Weight createWeight(IndexSearcher searcher, ScoreMode scoreMode, float boost) throws IOException;

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
	public Iterator<BooleanClause> iterator() {
		return clauses.iterator();
	}

	public String getField() {
		return this.field;
	}

	public List<BooleanClause> getClauses() {
		return this.clauses;
	}

}
