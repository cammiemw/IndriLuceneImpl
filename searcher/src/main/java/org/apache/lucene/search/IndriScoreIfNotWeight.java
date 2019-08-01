package org.apache.lucene.search;

import java.io.IOException;
import java.util.Set;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.Weight.DefaultBulkScorer;

public class IndriScoreIfNotWeight extends Weight {
	
	private final Weight excludeWeight;
	private final Weight queryWeight;
	
	public IndriScoreIfNotWeight(Query exclude, Query query, IndexSearcher searcher, ScoreMode scoreMode, float boost) throws IOException {
		super (query);
		this.excludeWeight = exclude.createWeight(searcher, scoreMode, boost);
		this.queryWeight = query.createWeight(searcher, scoreMode, boost);
	}


	@Override
	public boolean isCacheable(LeafReaderContext ctx) {
		// TODO Auto-generated method stub
		return false;
	}

	@Override
	public void extractTerms(Set<Term> terms) {
		// TODO Auto-generated method stub

	}

	@Override
	public Explanation explain(LeafReaderContext context, int doc) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
	
	private Scorer getScorer(LeafReaderContext context) throws IOException {
		Scorer scorer = new ReqExclScorer(queryWeight.scorer(context), excludeWeight.scorer(context));
		return scorer;
	}
	
	@Override
	public Scorer scorer(LeafReaderContext context) throws IOException {
		return getScorer(context);
	}

	@Override
	public BulkScorer bulkScorer(LeafReaderContext context) throws IOException {
		Scorer scorer = getScorer(context);
		if (scorer != null) {
			BulkScorer bulkScorer = new DefaultBulkScorer(scorer);
			return bulkScorer;
		}
		return null;
	}

}
