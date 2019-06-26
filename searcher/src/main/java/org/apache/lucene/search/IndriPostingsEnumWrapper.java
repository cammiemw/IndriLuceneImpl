package org.apache.lucene.search;

import java.io.IOException;

import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.util.BytesRef;

public class IndriPostingsEnumWrapper extends IndriDocAndPostingsIterator {

	private final PostingsEnum postings;
	private int currentPosition;

	public IndriPostingsEnumWrapper(PostingsEnum postings) {
		this.postings = postings;
		currentPosition = -1;
	}

	@Override
	public int docID() {
		return postings.docID();
	}

	@Override
	public int nextDoc() throws IOException {
		return postings.nextDoc();
	}

	@Override
	public int advance(int target) throws IOException {
		return postings.advance(target);
	}

	@Override
	public long cost() {
		return postings.cost();
	}

	@Override
	public int endPosition() throws IOException {
		return currentPosition;
	}

	@Override
	public int freq() throws IOException {
		return postings.freq();
	}

	@Override
	public int nextPosition() throws IOException {
		currentPosition = postings.nextPosition();
		return currentPosition;
	}

	@Override
	public int startOffset() throws IOException {
		return postings.startOffset();
	}

	@Override
	public int endOffset() throws IOException {
		return postings.endOffset();
	}

	@Override
	public BytesRef getPayload() throws IOException {
		return postings.getPayload();
	}

}
