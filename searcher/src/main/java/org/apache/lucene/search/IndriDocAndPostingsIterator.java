package org.apache.lucene.search;

import java.io.IOException;

import org.apache.lucene.index.PostingsEnum;

public abstract class IndriDocAndPostingsIterator extends PostingsEnum {

	@Override
	public abstract int docID();

	@Override
	public abstract int nextDoc() throws IOException;

	@Override
	public abstract int advance(int target) throws IOException;

	@Override
	public abstract long cost();

	public abstract int endPosition() throws IOException;

}
