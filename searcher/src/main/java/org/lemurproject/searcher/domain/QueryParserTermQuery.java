package org.lemurproject.searcher.domain;

public class QueryParserTermQuery extends QueryParserQuery {

	private String term;

	public String getTerm() {
		return term;
	}

	public void setTerm(String term) {
		this.term = term;
	}

}
