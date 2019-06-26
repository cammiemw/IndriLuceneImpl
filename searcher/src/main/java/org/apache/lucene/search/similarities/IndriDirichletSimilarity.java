package org.apache.lucene.search.similarities;

import java.util.List;
import java.util.Locale;

import org.apache.lucene.search.Explanation;

public class IndriDirichletSimilarity extends IndriSimilarity {

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
	protected double score(BasicStats stats, double freq, double docLen) {
		double collectionProbability = ((IndriStats) stats).getCollectionProbability();
		double score = (freq + (mu * collectionProbability)) / (docLen + mu);
		return (double) (Math.log(score));
	}

	@Override
	protected void explain(List<Explanation> subs, BasicStats stats, double freq, double docLen) {
		if (stats.getBoost() != 1.0f) {
			subs.add(Explanation.match(stats.getBoost(), "boost"));
		}

		subs.add(Explanation.match(mu, "mu"));
		Explanation weightExpl = Explanation.match(
				(float) Math.log(1 + freq / (mu * ((IndriStats) stats).getCollectionProbability())), "term weight");
		subs.add(weightExpl);
		subs.add(Explanation.match((float) Math.log(mu / (docLen + mu)), "document norm"));
		super.explain(subs, stats, freq, docLen);
	}

	/** Returns the &mu; parameter. */
	public float getMu() {
		return mu;
	}

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
		public double computeProbability(BasicStats stats) {
			return ((double) stats.getTotalTermFreq()) / ((double) stats.getNumberOfFieldTokens());
		}

		@Override
		public String getName() {
			return null;
		}
	}

}
