package org.apache.lucene.search;

import java.io.IOException;

public class IndriTermScorerWrapper extends Scorer implements SmoothingScorer, WeightedScorer {

	private final Scorer termScorer;
	private final LeafSimScorer docScorer;
	private final float boost;

	protected IndriTermScorerWrapper(Weight weight, LeafSimScorer docScorer, Scorer termScorer, float boost) {
		super(weight);
		this.docScorer = docScorer;
		this.termScorer = termScorer;
		this.boost = boost;
		// TODO Auto-generated constructor stub
	}

	@Override
	public float smoothingScore(DisiWrapper topList, int docId) throws IOException {
		return docScorer.score(docId, 0);
	}

	@Override
	public int docID() {
		int docId = termScorer.docID();
		return docId;
	}

	@Override
	public float score() throws IOException {
		return termScorer.score();
	}

	@Override
	public DocIdSetIterator iterator() {
		return termScorer.iterator();
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
