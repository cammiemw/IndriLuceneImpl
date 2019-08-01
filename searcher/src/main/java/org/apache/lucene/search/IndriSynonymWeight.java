package org.apache.lucene.search;

import java.io.IOException;
import java.util.List;

import org.lemurproject.searcher.IndriSynonymQuery;

public class IndriSynonymWeight extends IndriBeliefOpWeight {

	public IndriSynonymWeight(IndriSynonymQuery query, IndexSearcher searcher, String field, float boost)
			throws IOException {
		super(query, searcher, field, boost);
	}

	protected IndriInvertedList createInvertedList(List<IndriDocAndPostingsIterator> iterators) throws IOException {
		IndriInvertedList invList = new IndriInvertedList(getField());

		for (IndriDocAndPostingsIterator iterator : iterators) {
			while (iterator.docID() != DocIdSetIterator.NO_MORE_DOCS) {
				int docId = iterator.nextDoc();
				for (int i = 0; i < iterator.freq(); i++) {
					int startPostion = iterator.nextPosition();
					int endPostion = iterator.endPosition();
					invList.addPosting(docId, startPostion, endPostion);
				}
			}
		}
		return invList;

	}

}
