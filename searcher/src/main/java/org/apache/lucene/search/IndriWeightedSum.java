package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.Similarity;
import org.lemurproject.searcher.IndriWeightedSumQuery;

public class IndriWeightedSum extends Weight {
	/** The Similarity implementation. */
	final Similarity similarity;
	final IndriWeightedSumQuery query;

	final ArrayList<Weight> weights;
	final boolean needsScores;
	final float boost;

	public IndriWeightedSum(IndriWeightedSumQuery query, IndexSearcher searcher, boolean needsScores, float boost)
			throws IOException {
		super(query);
		this.query = query;
		this.boost = boost;
		this.needsScores = needsScores;
		this.similarity = searcher.getSimilarity(needsScores);
		weights = new ArrayList<>();
		for (BooleanClause c : query) {
			Weight w = searcher.createWeight(c.getQuery(), needsScores && c.isScoring(), boost);
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

	@Override
	public Scorer scorer(LeafReaderContext context) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public BulkScorer bulkScorer(LeafReaderContext context) throws IOException {
		List<Scorer> subScorers = new ArrayList<>();
		Iterator<BooleanClause> cIter = query.iterator();
		for (Weight w : weights) {
			BooleanClause c = cIter.next();
			Scorer scorer = w.scorer(context);
			if (scorer != null) {
				subScorers.add(scorer);
			}
		}

		Scorer scorer = subScorers.get(0);
		if (subScorers.size() > 1) {
			scorer = new IndriWeightedSumScorer(this, subScorers, true);
		}
		BulkScorer bulkScorer = new DefaultBulkScorer(scorer);
		return bulkScorer;
	}

}
