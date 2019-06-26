package org.apache.lucene.search.similarities;

import java.util.List;
import java.util.Locale;

import org.apache.lucene.search.Explanation;
import org.apache.lucene.search.similarities.BasicStats;
import org.apache.lucene.search.similarities.LMSimilarity;

/**
 * Language model based on the Jelinek-Mercer smoothing method. From Chengxiang
 * Zhai and John Lafferty. 2001. A study of smoothing methods for language
 * models applied to Ad Hoc information retrieval. In Proceedings of the 24th
 * annual international ACM SIGIR conference on Research and development in
 * information retrieval (SIGIR '01). ACM, New York, NY, USA, 334-342.
 * <p>
 * The model has a single parameter, &lambda;. According to said paper, the
 * optimal value depends on both the collection and the query. The optimal value
 * is around {@code 0.1} for title queries and {@code 0.7} for long queries.
 * </p>
 *
 * @lucene.experimental
 */
public class IndriJelinekMercerSimilarity extends LMSimilarity {
	/** The &lambda; parameter. */
	private final float lambda;

	/** Instantiates with the specified collectionModel and &lambda; parameter. */
	public IndriJelinekMercerSimilarity(CollectionModel collectionModel, float lambda) {
		super(collectionModel);
		this.lambda = lambda;
	}

	/** Instantiates with the specified &lambda; parameter. */
	public IndriJelinekMercerSimilarity(float lambda) {
		super(new IndriCollectionModel());
		this.lambda = lambda;
	}

	public IndriJelinekMercerSimilarity() {
		this(.4f);
	}

	@Override
	protected double score(BasicStats stats, double freq, double docLen) {
		return (double) (Math
				.log(((1 - lambda) * freq / docLen) + (lambda * ((LMStats) stats).getCollectionProbability())));
	}

	@Override
	protected void explain(List<Explanation> subs, BasicStats stats, double freq, double docLen) {
		if (stats.getBoost() != 1.0f) {
			subs.add(Explanation.match(stats.getBoost(), "boost"));
		}
		subs.add(Explanation.match(lambda, "lambda"));
		super.explain(subs, stats, freq, docLen);
	}

	/** Returns the &lambda; parameter. */
	public float getLambda() {
		return lambda;
	}

	@Override
	public String getName() {
		return String.format(Locale.ROOT, "Jelinek-Mercer(%f)", getLambda());
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
			return ((float) stats.getTotalTermFreq()) / ((float) stats.getNumberOfFieldTokens());
		}

		@Override
		public String getName() {
			return null;
		}
	}
}
