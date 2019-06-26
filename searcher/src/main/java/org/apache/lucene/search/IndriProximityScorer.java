package org.apache.lucene.search;

import java.io.IOException;

import org.apache.lucene.search.DisiWrapper;
import org.apache.lucene.search.DocIdSetIterator;
import org.apache.lucene.search.LeafSimScorer;
import org.apache.lucene.search.Scorer;
import org.apache.lucene.search.Weight;

public class IndriProximityScorer extends Scorer implements SmoothingScorer, WeightedScorer {

	private final IndriProximityEnum postingsEnum;
	private final LeafSimScorer docScorer;

	protected IndriProximityScorer(Weight weight, IndriProximityEnum postingsEnum, LeafSimScorer docScorer) {
		super(weight);
		this.docScorer = docScorer;
		this.postingsEnum = postingsEnum;
	}

	@Override
	public int docID() {
		return postingsEnum.docID();
	}

	final int freq() throws IOException {
		return postingsEnum.freq();
	}

	@Override
	public DocIdSetIterator iterator() {
		return postingsEnum;
	}

	@Override
	public float score() throws IOException {
		assert docID() != DocIdSetIterator.NO_MORE_DOCS;
		return docScorer.score(postingsEnum.docID(), postingsEnum.freq());
	}

	/** Returns a string representation of this <code>TermScorer</code>. */
	@Override
	public String toString() {
		return "scorer(" + weight + ")[" + super.toString() + "]";
	}

	@Override
	public float getBoost() {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float smoothingScore(DisiWrapper topList, int docId) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public float getMaxScore(int upTo) throws IOException {
		// TODO Auto-generated method stub
		return 0;
	}

}
