package org.lemurproject.searcher;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.IndriWindowWeight;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Weight;

public class IndriWindowQuery extends IndriProximityQuery {

	public IndriWindowQuery(List<BooleanClause> clauses, String field, int distance) {
		super(clauses, field, distance);
	}

	@Override
	public Weight createWeight(IndexSearcher searcher, ScoreMode scoreMode, float boost) throws IOException {
		return new IndriWindowWeight(this, searcher, getField(), getDistance(), boost);
	}

}
