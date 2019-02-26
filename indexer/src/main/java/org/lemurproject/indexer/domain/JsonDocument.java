package org.lemurproject.indexer.domain;

public class JsonDocument extends BaseObject {

	private String docno;
	private String text;

	public String getDocno() {
		return docno;
	}

	public void setDocno(String docno) {
		this.docno = docno;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

}
