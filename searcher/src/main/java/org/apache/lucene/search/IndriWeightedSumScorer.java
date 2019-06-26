package org.apache.lucene.search;

import java.io.IOException;
import java.util.List;

import org.apache.lucene.search.DisiWrapper;
import org.apache.lucene.search.DisjunctionScorer;
import org.apache.lucene.search.ScoreMode;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

public class IndriWeightedSumScorer extends DisjunctionScorer implements SmoothingScorer {
	private DisiWrapper subAvgScorers;

	protected IndriWeightedSumScorer(Weight weight, List<Scorer> subScorers, ScoreMode scoreMode) throws IOException {
		super(weight, subScorers, scoreMode);
		this.subAvgScorers = null;
		DisiWrapper prevWrapper = null;
		for (Scorer scorer : subScorers) {
			final DisiWrapper w = new DisiWrapper(scorer);
			if (subAvgScorers == null) {
				subAvgScorers = w;
			} else {
				prevWrapper.next = w;
			}
			prevWrapper = w;
		}
	}

	@Override
	protected float score(DisiWrapper topList) throws IOException {
		double score = 0;
		double boostSum = 0.0;
		for (DisiWrapper w = topList; w != null; w = w.next) {
			int docId = this.docID();
			int scorerDocId = w.scorer.docID();
			if (docId == scorerDocId) {
				score += ((WeightedScorer) w.scorer).getBoost() * Math.exp(w.scorer.score());
			} else if (w.scorer instanceof SmoothingScorer) {
				double smoothingScore = ((WeightedScorer) w.scorer).getBoost()
						* Math.exp(((SmoothingScorer) w.scorer).smoothingScore(topList, docId));
				score += smoothingScore;
			}
			if (w.scorer instanceof WeightedScorer) {
				boostSum += ((WeightedScorer) w.scorer).getBoost();
			} else {
				boostSum++;
			}
		}
		return (float) (Math.log((score / boostSum)));
	}

	@Override
	DisiWrapper getSubMatches() throws IOException {
		return subAvgScorers;
	}

	@Override
	public float smoothingScore(DisiWrapper topList, int docId) throws IOException {
		DisiWrapper test = getSubMatches();
		double score = 0;
		double boostSum = 0.0;
		for (DisiWrapper w = test; w != null; w = w.next) {
			int scorerDocId = w.scorer.docID();
			if (docId == scorerDocId) {
				score += ((WeightedScorer) w.scorer).getBoost() * Math.exp(w.scorer.score());
			} else if (w.scorer instanceof SmoothingScorer) {
				double smoothingScore = ((WeightedScorer) w.scorer).getBoost()
						* Math.exp(((SmoothingScorer) w.scorer).smoothingScore(topList, docId));
				score += smoothingScore;
			}
			if (w.scorer instanceof WeightedScorer) {
				boostSum += ((WeightedScorer) w.scorer).getBoost();
			} else {
				boostSum++;
			}
		}
		return (float) (Math.log((score / boostSum)));
	}

	@Override
	public float getMaxScore(int upTo) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
