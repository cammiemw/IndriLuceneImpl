package org.lemurproject.searcher;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.IndriNearWeight;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Weight;

public class IndriNearQuery extends IndriProximityQuery {

	private final int distance;

	public IndriNearQuery(List<BooleanClause> clauses, String field, int distance) {
		super(clauses, field);
		this.distance = distance;
	}

	@Override
	public Weight createWeight(IndexSearcher searcher, ScoreMode scoreMode, float boost) throws IOException {
		return new IndriNearWeight(this, searcher, getField(), distance, boost);
	}

}
