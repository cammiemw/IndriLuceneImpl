package org.apache.lucene.search;

import java.io.IOException;

import org.apache.lucene.search.DisiWrapper;

public interface SmoothingScorer {

	float smoothingScore(DisiWrapper topList, int docId) throws IOException;

}
