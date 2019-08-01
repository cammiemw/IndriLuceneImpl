package org.lemurproject.searcher.domain;

import org.apache.lucene.search.BooleanClause.Occur;

public abstract class QueryParserQuery {

	private String type; // Either operator or term
	private Float boost;
	private String field;
	private Occur occur;

	public Float getBoost() {
		return boost;
	}

	public void setBoost(Float boost) {
		this.boost = boost;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getField() {
		return field;
	}

	public void setField(String field) {
		this.field = field;
	}

	public Occur getOccur() {
		return occur;
	}

	public void setOccur(Occur occur) {
		this.occur = occur;
	}

}
