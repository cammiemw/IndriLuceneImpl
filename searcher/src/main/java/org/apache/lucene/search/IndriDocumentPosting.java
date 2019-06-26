package org.apache.lucene.search;

public class IndriDocumentPosting {

	private int start;
	private int end;

	public IndriDocumentPosting(Integer start, Integer end) {
		this.start = start.intValue();
		this.end = end.intValue();
	}

	public IndriDocumentPosting(int start, int end) {
		this.start = start;
		this.end = end;
	}

	public int getStart() {
		return start;
	}

	public void setStart(int start) {
		this.start = start;
	}

	public int getEnd() {
		return end;
	}

	public void setEnd(int end) {
		this.end = end;
	}

}
