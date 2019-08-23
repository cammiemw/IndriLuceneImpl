package org.lemurproject.lucene6;

import java.nio.file.Paths;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class SearchPOC {

	public static void main(String[] args) throws Exception {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		QueryParser qp = new QueryParser("externalId", analyzer);
		Query q = qp.parse("clueweb09-en0000-00-20836");
		// Query q = qp.parse("pennsyltucky");

		int hitsPerPage = 10;
		Directory dir = FSDirectory.open(Paths.get("C:\\dev\\Indexes_Lucene_1.0\\cw_speed"));
		IndexReader reader = DirectoryReader.open(dir);

		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(q, hitsPerPage);
		ScoreDoc[] hits = docs.scoreDocs;

		// System.out.println("Found " + hits.length + " hits.");
		// for (int i = 0; i < hits.length; ++i) {
		int docId = hits[0].doc;
		Document d = searcher.doc(docId);
		System.out.println((0 + 1) + ". " + d.get("internalId") + "\t" + d.get("externalId"));
		System.out.println(d.get("fulltext"));
		// }

	}

}
