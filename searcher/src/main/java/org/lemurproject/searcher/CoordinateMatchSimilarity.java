package org.lemurproject.searcher;

import java.io.IOException;

import org.apache.lucene.index.FieldInvertState;
import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.search.CollectionStatistics;
import org.apache.lucene.search.TermStatistics;
import org.apache.lucene.search.similarities.Similarity;
import org.apache.lucene.util.BytesRef;

public class CoordinateMatchSimilarity extends Similarity {

	@Override
	public long computeNorm(FieldInvertState state) {
		// TODO Auto-generated method stub
		return 0;
	}

	@Override
	public SimScorer simScorer(SimWeight weight, LeafReaderContext context) throws IOException {
		return new CMSimScorer();
	}

	private static class CMStats extends SimWeight {

//		@Override
//		public float getValueForNormalization() {
//			return 0;
//		}
//
//		@Override
//		public void normalize(float queryNorm, float boost) {
//		}
	}

	private final class CMSimScorer extends SimScorer {

		@Override
		public float score(int doc, float freq) {
			System.out.println(doc);
			if (freq > 0) {
				return 1;
			}
			return 0;
		}

		@Override
		public float computeSlopFactor(int distance) {
			// TODO Auto-generated method stub
			return 0;
		}

		@Override
		public float computePayloadFactor(int doc, int start, int end, BytesRef payload) {
			// TODO Auto-generated method stub
			return 0;
		}

	}

	@Override
	public SimWeight computeWeight(float boost, CollectionStatistics collectionStats, TermStatistics... termStats) {
		return new CMStats();
	}

}
