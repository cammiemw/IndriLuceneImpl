package org.apache.lucene.search;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import org.apache.lucene.index.LeafReaderContext;
import org.apache.lucene.index.PostingsEnum;
import org.apache.lucene.index.Term;
import org.apache.lucene.search.similarities.Similarity;
import org.lemurproject.searcher.IndriProximityQuery;

public abstract class IndriBeliefOpWeight extends Weight {

	private final IndriProximityQuery query;
	private final ArrayList<Weight> weights;
	private final String field;
	private final float boost;
	private final Similarity similarity;
	private CollectionStatistics collectionStats;
	private Similarity.SimScorer simScorer;

	protected IndriBeliefOpWeight(IndriProximityQuery query, IndexSearcher searcher, String field,
			float boost) throws IOException {
		super(query);
		this.query = query;
		this.field = field;
		this.boost = boost;
		this.similarity = searcher.getSimilarity();
		collectionStats = searcher.collectionStatistics(field);
		weights = new ArrayList<>();
		for (BooleanClause c : query) {
			Weight w = searcher.createWeight(c.getQuery(), ScoreMode.COMPLETE, boost);
			weights.add(w);
		}
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

	protected Scorer getScorer(LeafReaderContext context) throws IOException {
		List<IndriDocAndPostingsIterator> iterators = new ArrayList<>();
		for (Weight w : weights) {
			Scorer scorer = w.scorer(context);
			if (scorer != null) {
				IndriDocAndPostingsIterator iterator = null;
				if (scorer.iterator() instanceof IndriProximityEnum) {
					iterator = ((IndriProximityEnum) scorer.iterator());
				} else if (scorer.iterator() instanceof PostingsEnum) {
					iterator = new IndriPostingsEnumWrapper((PostingsEnum) scorer.iterator());
				}
				iterators.add(iterator);
			}
		}

		if (iterators.isEmpty()) {
			return null;
		}

		IndriProximityEnum postingsEnum = getProximityIterator(iterators);
		TermStatistics termStats = postingsEnum.getInvList().getTermStatistics();
		this.simScorer = similarity.scorer(boost, collectionStats, termStats);
		LeafSimScorer leafScorer = new LeafSimScorer(simScorer, context.reader(), field, true);
		Scorer scorer = new IndriProximityScorer(this, postingsEnum, leafScorer);
		return scorer;
	}

	protected IndriProximityEnum getProximityIterator(List<IndriDocAndPostingsIterator> iterators)
			throws IOException {
		IndriInvertedList invList = createInvertedList(iterators);
		IndriProximityEnum nearPostings = new IndriProximityEnum(invList);

		return nearPostings;
	}
	
	protected abstract IndriInvertedList createInvertedList(List<IndriDocAndPostingsIterator> iterators)
			throws IOException;

	public String getField() {
		return field;
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
