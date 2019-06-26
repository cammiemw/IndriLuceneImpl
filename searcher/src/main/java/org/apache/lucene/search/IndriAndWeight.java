package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.Similarity;
import org.lemurproject.searcher.IndriAndQuery;

public class IndriAndWeight extends Weight {
	/** The Similarity implementation. */
	private final Similarity similarity;
	private final IndriAndQuery query;

	private final ArrayList<Weight> weights;
	private final ScoreMode scoreMode;
	private final float boost;

	public IndriAndWeight(IndriAndQuery query, IndexSearcher searcher, ScoreMode scoreMode, float boost)
			throws IOException {
		super(query);
		this.query = query;
		this.boost = boost;
		this.scoreMode = scoreMode;
		this.similarity = searcher.getSimilarity();
		weights = new ArrayList<>();
		for (BooleanClause c : query) {
			Weight w = searcher.createWeight(c.getQuery(), scoreMode, boost);
			weights.add(w);
		}
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
		List<Scorer> subScorers = new ArrayList<>();

		for (Weight w : weights) {
			Scorer scorer = w.scorer(context);
			if (scorer != null) {
				subScorers.add(scorer);
			}
		}

		if (subScorers.isEmpty()) {
			return null;
		}
		Scorer scorer = subScorers.get(0);
		if (subScorers.size() > 1) {
			scorer = new IndriAndScorer(this, subScorers, scoreMode, this.boost);
		}
		return scorer;
	}

	@Override
	public Scorer scorer(LeafReaderContext context) throws IOException {
		return getScorer(context);
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
