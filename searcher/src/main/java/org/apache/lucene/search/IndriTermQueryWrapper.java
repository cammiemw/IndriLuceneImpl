package org.apache.lucene.search;

import java.io.IOException;
import java.util.Objects;
import java.util.Set;

import org.apache.lucene.index.IndexReaderContext;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.index.TermContext;
import org.apache.lucene.search.similarities.Similarity;

public class IndriTermQueryWrapper extends Query {

	protected final TermQuery termQuery;
	private final TermContext perReaderTermState;

	final class IndriTermWeightWrapper extends Weight {
		private final Weight termWeight;
		private final Similarity similarity;
		private final Similarity.SimWeight stats;
		private final float boost;

		public IndriTermWeightWrapper(IndexSearcher searcher, boolean needsScores, float boost, TermContext termStates)
				throws IOException {
			super(termQuery);
			this.boost = boost;
			this.termWeight = termQuery.createWeight(searcher, needsScores, boost);
			this.similarity = searcher.getSimilarity(needsScores);

			final CollectionStatistics collectionStats;
			final TermStatistics termStats;
			if (needsScores) {
				collectionStats = searcher.collectionStatistics(termQuery.getTerm().field());
				termStats = searcher.termStatistics(termQuery.getTerm(), termStates);
			} else {
				// we do not need the actual stats, use fake stats with docFreq=maxDoc and
				// ttf=-1
				final int maxDoc = searcher.getIndexReader().maxDoc();
				collectionStats = new CollectionStatistics(termQuery.getTerm().field(), maxDoc, -1, -1, -1);
				termStats = new TermStatistics(termQuery.getTerm().bytes(), maxDoc, -1);
			}

			this.stats = similarity.computeWeight(boost, collectionStats, termStats);
		}

		@Override
		public void extractTerms(Set<Term> terms) {
			termWeight.extractTerms(terms);
		}

		@Override
		public Matches matches(LeafReaderContext context, int doc) throws IOException {
			return termWeight.matches(context, doc);
		}

		@Override
		public String toString() {
			return termWeight.toString();
		}

		@Override
		public Scorer scorer(LeafReaderContext context) throws IOException {
			Scorer termScorer = termWeight.scorer(context);
			Scorer indriTermScorer = new IndriTermScorerWrapper(termWeight, similarity.simScorer(stats, context),
					termScorer, this.boost);
			return indriTermScorer;
		}

		@Override
		public boolean isCacheable(LeafReaderContext ctx) {
			return termWeight.isCacheable(ctx);
		}

		@Override
		public Explanation explain(LeafReaderContext context, int doc) throws IOException {
			return this.termWeight.explain(context, doc);
		}
	}

	/** Constructs a query for the term <code>t</code>. */
	public IndriTermQueryWrapper(Term t) {
		this.termQuery = new TermQuery(t);
		this.perReaderTermState = null;
	}

	/**
	 * Expert: constructs a TermQuery that will use the provided docFreq instead of
	 * looking up the docFreq against the searcher.
	 */
	public IndriTermQueryWrapper(Term t, TermContext states) {
		this.termQuery = new TermQuery(t, states);
		this.perReaderTermState = Objects.requireNonNull(states);
	}

	@Override
	public Weight createWeight(IndexSearcher searcher, boolean needsScores, float boost) throws IOException {
		final IndexReaderContext context = searcher.getTopReaderContext();
		final TermContext termState;
		if (perReaderTermState == null || perReaderTermState.wasBuiltFor(context) == false) {
			if (needsScores) {
				// make TermQuery single-pass if we don't have a PRTS or if the context
				// differs!
				termState = TermContext.build(context, termQuery.getTerm());
			} else {
				// do not compute the term state, this will help save seeks in the terms
				// dict on segments that have a cache entry for this query
				termState = null;
			}
		} else {
			// PRTS was pre-build for this IS
			termState = this.perReaderTermState;
		}

		return new IndriTermWeightWrapper(searcher, needsScores, boost, termState);
	}

	/** Returns the term of this query. */
	public Term getTerm() {
		return this.termQuery.getTerm();
	}

	/**
	 * Returns the {@link TermContext} passed to the constructor, or null if it was
	 * not passed.
	 *
	 * @lucene.experimental
	 */
	public TermContext getTermContext() {
		return this.getTermContext();
	}

	@Override
	public String toString(String field) {
		return this.termQuery.toString();
	}

	@Override
	public boolean equals(Object obj) {
		return this.termQuery.equals(obj);
	}

	@Override
	public int hashCode() {
		return this.termQuery.hashCode();
	}

}
