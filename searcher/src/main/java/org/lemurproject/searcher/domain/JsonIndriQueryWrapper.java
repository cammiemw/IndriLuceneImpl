package org.lemurproject.searcher.domain;

import java.util.List;

public class JsonIndriQueryWrapper extends BaseObject {

	private String index;
	private String rule;
	private Integer count;
	private List<JsonIndriQuery> queries;

	public String getIndex() {
		return index;
	}

	public void setIndex(String index) {
		this.index = index;
	}

	public String getRule() {
		return rule;
	}

	public void setRule(String rule) {
		this.rule = rule;
	}

	public Integer getCount() {
		return count;
	}

	public void setCount(Integer count) {
		this.count = count;
	}

	public List<JsonIndriQuery> getQueries() {
		return queries;
	}

	public void setQueries(List<JsonIndriQuery> queries) {
		this.queries = queries;
	}

}
