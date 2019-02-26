package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.Similarity;
import org.lemurproject.searcher.IndriOrQuery;

public class IndriOrWeight extends Weight {
	/** The Similarity implementation. */
	final Similarity similarity;
	final IndriOrQuery query;

	final ArrayList<Weight> weights;
	final boolean needsScores;
	final float boost;

	public IndriOrWeight(IndriOrQuery query, IndexSearcher searcher, boolean needsScores, float boost)
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

	private Scorer getScorer(LeafReaderContext context) throws IOException {
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
			scorer = new IndriOrScorer(this, subScorers, needsScores);
		}
		return scorer;
	}

	@Override
	public Scorer scorer(LeafReaderContext context) throws IOException {
		return getScorer(context);
		// return null;
	}

	@Override
	public BulkScorer bulkScorer(LeafReaderContext context) throws IOException {
		Scorer scorer = getScorer(context);
		BulkScorer bulkScorer = new DefaultBulkScorer(scorer);
		return bulkScorer;
	}

}
