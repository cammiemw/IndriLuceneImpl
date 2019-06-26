package org.apache.lucene.search;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.Similarity;
import org.lemurproject.searcher.IndriNotQuery;

public class IndriNotWeight extends Weight {
	/** The Similarity implementation. */
	private final Similarity similarity;
	private final IndriNotQuery query;

	private final Weight weight;
	private final ScoreMode scoreMode;
	private final float boost;

	public IndriNotWeight(IndriNotQuery query, IndexSearcher searcher, ScoreMode scoreMode, float boost)
			throws IOException {
		super(query);
		this.query = query;
		this.boost = boost;
		this.scoreMode = scoreMode;
		this.similarity = searcher.getSimilarity();
		// Not Query only has one clause
		BooleanClause c = query.iterator().next();
		weight = searcher.createWeight(c.getQuery(), scoreMode, boost);
	}

	@Override
	public boolean isCacheable(LeafReaderContext ctx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void extractTerms(Set<Term> terms) {
		// TODO Auto-generated method stub

	}

	@Override
	public Explanation explain(LeafReaderContext context, int doc) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	private Scorer getScorer(LeafReaderContext context) throws IOException {
		Scorer scorer = weight.scorer(context);
		if (scorer != null) {
			Scorer scorerWrapper = new IndriNotScorer(this, scorer);
			return scorerWrapper;
		}
		return null;
	}

	@Override
	public Scorer scorer(LeafReaderContext context) throws IOException {
		return getScorer(context);
		// return null;
	}

	@Override
	public BulkScorer bulkScorer(LeafReaderContext context) throws IOException {
		Scorer scorer = getScorer(context);
		if (scorer != null) {
			BulkScorer bulkScorer = new DefaultBulkScorer(scorer);
			return bulkScorer;
		}
		return null;
	}

}
