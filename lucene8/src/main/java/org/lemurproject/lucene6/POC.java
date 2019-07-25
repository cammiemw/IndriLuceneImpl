package org.lemurproject.lucene6;

import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.FSDirectory;
import org.apache.lucene.store.NoLockFactory;
import org.apache.lucene.store.SimpleFSDirectory;

public class POC {

	private static String INDEX_DIR = "C:/dev/Indexes_Lucene_1.0/lucene8poc";

	public static void main(String[] args) throws Exception {
		if (args.length > 0) {
			INDEX_DIR = args[0];
		}

		IndexWriter writer = createWriter();
		List<Document> documents = new ArrayList<Document>();

		Document document1 = createDocument(1, "Lokesh", "Gupta", "howtodoinjava.com");
		documents.add(document1);

		Document document2 = createDocument(2, "Brian", "Schultz", "example.com");
		documents.add(document2);

		// Let's clean everything first
		writer.deleteAll();

		writer.addDocuments(documents);
		writer.commit();
		writer.close();
	}

	private static Document createDocument(Integer id, String firstName, String lastName, String website) {
		Document document = new Document();
		document.add(new StringField("id", id.toString(), Field.Store.YES));
		document.add(new TextField("firstName", firstName, Field.Store.YES));
		document.add(new TextField("lastName", lastName, Field.Store.YES));
		document.add(new TextField("website", website, Field.Store.YES));
		return document;
	}

	private static IndexWriter createWriter() throws IOException {
		// Directory dir = FSDirectory.open//FSDirectory.open(Paths.get(INDEX_DIR));
		FSDirectory dir = new SimpleFSDirectory(Paths.get(INDEX_DIR), NoLockFactory.INSTANCE);
		IndexWriterConfig config = new IndexWriterConfig(new StandardAnalyzer());
		IndexWriter writer = new IndexWriter(dir, config);
		return writer;
	}

}
