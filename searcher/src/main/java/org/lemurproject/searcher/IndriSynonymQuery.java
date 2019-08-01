package org.lemurproject.searcher;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.BooleanClause;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.IndriSynonymWeight;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Weight;

public class IndriSynonymQuery extends IndriProximityQuery {

	public IndriSynonymQuery(List<BooleanClause> clauses, String field) {
		super(clauses, field);
	}

	@Override
	public Weight createWeight(IndexSearcher searcher, ScoreMode scoreMode, float boost) throws IOException {
		return new IndriSynonymWeight(this, searcher, getField(), boost);
	}

}
