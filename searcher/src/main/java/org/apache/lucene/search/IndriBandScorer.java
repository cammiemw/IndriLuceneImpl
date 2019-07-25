package org.apache.lucene.search;

import java.io.IOException;
import java.util.List;

public class IndriBandScorer extends ConjunctionScorer implements WeightedScorer {
	private float boost;

	protected IndriBandScorer(Weight weight, List<Scorer> subScorers, ScoreMode scoreMode, float boost)
			throws IOException {
		super(weight, subScorers, subScorers);
	}

	@Override
	public float score() throws IOException {
		double score = 0;
		for (Scorer scorer : scorers) {
			double tempScore = scorer.score();
			score += tempScore;
		}
		return (float) (score / scorers.length);
	}

	@Override
	public float getBoost() {
		return this.boost;
	}

	@Override
	public float getMaxScore(int upTo) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
