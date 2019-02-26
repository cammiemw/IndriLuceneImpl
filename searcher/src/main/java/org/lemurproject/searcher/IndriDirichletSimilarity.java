package org.lemurproject.searcher;

import java.util.List;
import java.util.Locale;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMSimilarity;

public class IndriDirichletSimilarity extends LMSimilarity {

	/** The &mu; parameter. */
	private final float mu;

	/** Instantiates the similarity with the provided &mu; parameter. */
	public IndriDirichletSimilarity(CollectionModel collectionModel, float mu) {
		super(collectionModel);
		this.mu = mu;
	}

	/** Instantiates the similarity with the provided &mu; parameter. */
	public IndriDirichletSimilarity(float mu) {
		this.mu = mu;
	}

	/** Instantiates the similarity with the default &mu; value of 2000. */
	public IndriDirichletSimilarity(CollectionModel collectionModel) {
		this(collectionModel, 2000);
	}

	/** Instantiates the similarity with the default &mu; value of 2000. */
	public IndriDirichletSimilarity() {
		this(new IndriCollectionModel(), 2000);
	}

	@Override
	protected float score(BasicStats stats, float freq, float docLen) {
		float collectionProbability = ((LMStats) stats).getCollectionProbability();
		float score = (freq + (mu * collectionProbability)) / (docLen + mu);
		return (float) (/* stats.getBoost() **/ Math.log(score));
	}

	@Override
	protected void explain(List<Explanation> subs, BasicStats stats, int doc, float freq, float docLen) {
		if (stats.getBoost() != 1.0f) {
			subs.add(Explanation.match(stats.getBoost(), "boost"));
		}

		subs.add(Explanation.match(mu, "mu"));
		Explanation weightExpl = Explanation
				.match((float) Math.log(1 + freq / (mu * ((LMStats) stats).getCollectionProbability())), "term weight");
		subs.add(weightExpl);
		subs.add(Explanation.match((float) Math.log(mu / (docLen + mu)), "document norm"));
		super.explain(subs, stats, doc, freq, docLen);
	}

	/** Returns the &mu; parameter. */
	public float getMu() {
		return mu;
	}

	@Override
	public String getName() {
		return String.format(Locale.ROOT, "Dirichlet(%f)", getMu());
	}

	/**
	 * Models {@code p(w|C)} as the number of occurrences of the term in the
	 * collection, divided by the total number of tokens {@code + 1}.
	 */
	public static class IndriCollectionModel implements CollectionModel {

		/** Sole constructor: parameter-free */
		public IndriCollectionModel() {
		}

		@Override
		public float computeProbability(BasicStats stats) {
			return ((float) stats.getTotalTermFreq()) / ((float) stats.getNumberOfFieldTokens());
		}

		@Override
		public String getName() {
			return null;
		}
	}

}
