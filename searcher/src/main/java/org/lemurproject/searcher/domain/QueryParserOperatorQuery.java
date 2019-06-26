package org.lemurproject.searcher.domain;

import java.util.ArrayList;
import java.util.List;

public class QueryParserOperatorQuery extends QueryParserQuery {

	private String operator;
	private Integer distance;
	private List<QueryParserQuery> subqueries;

	public String getOperator() {
		return operator;
	}

	public void setOperator(String operator) {
		this.operator = operator;
	}

	public Integer getDistance() {
		return distance;
	}

	public void setDistance(Integer distance) {
		this.distance = distance;
	}

	public List<QueryParserQuery> getSubqueries() {
		return subqueries;
	}

	public void setSubqueries(List<QueryParserQuery> subqueries) {
		this.subqueries = subqueries;
	}

	public void addSubquery(QueryParserQuery subquery, Float weight) {
		subquery.setBoost(weight);
		if (this.subqueries == null) {
			this.subqueries = new ArrayList<QueryParserQuery>();
		}
		this.subqueries.add(subquery);
	}

}
