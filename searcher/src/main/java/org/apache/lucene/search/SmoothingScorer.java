package org.apache.lucene.search;

import java.io.IOException;

public interface SmoothingScorer {

	float smoothingScore(DisiWrapper topList, int docId) throws IOException;

}
