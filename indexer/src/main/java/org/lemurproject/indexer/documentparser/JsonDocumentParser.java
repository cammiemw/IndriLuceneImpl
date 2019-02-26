package org.lemurproject.indexer.documentparser;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;

import org.lemurproject.indexer.domain.JsonDocument;
import org.lemurproject.indexer.domain.ParsedDocument;
import org.lemurproject.indexer.domain.ParsedDocumentField;
import org.xml.sax.SAXException;

import com.google.gson.Gson;

public class JsonDocumentParser implements DocumentParser {

	private final static String EXTERNALID_FIELD = "externalId";
	private final static String ID_FIELD = "internalId";
	private final static String BODY_FIELD = "body";

	private int docNum;
	private Iterator<File> fileIterator;
	private BufferedReader br;
	private String nextLine;
	private Gson gson;

	public JsonDocumentParser(String dataDirectory) throws IOException {
		gson = new Gson();
		File folder = Paths.get(dataDirectory).toFile();
		fileIterator = Arrays.asList(folder.listFiles()).iterator();
		getNextScanner();
		docNum = 0;
	}

	private void getNextScanner() throws IOException {
		if (fileIterator.hasNext()) {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(fileIterator.next())));
		} else {
			br = null;
		}
	}

	@Override
	public boolean hasNextDocument() {
		return fileIterator.hasNext() || nextLine != null;
	}

	@Override
	public ParsedDocument getNextDocument() throws IOException, SAXException {
		if (br != null) {
			if ((nextLine = br.readLine()) == null) {
				br.close();
				getNextScanner();
				if (br != null) {
					nextLine = br.readLine();
				}
			}
			if (nextLine != null) {
				docNum++;

				JsonDocument jsonDoc = gson.fromJson(nextLine, JsonDocument.class);

				ParsedDocument doc = new ParsedDocument();
				doc.setDocumentFields(new ArrayList<>());

				ParsedDocumentField internalIdField = new ParsedDocumentField(ID_FIELD, String.valueOf(docNum), false);
				doc.getDocumentFields().add(internalIdField);

				ParsedDocumentField externalIdField = new ParsedDocumentField(EXTERNALID_FIELD, jsonDoc.getDocno(),
						false);
				doc.getDocumentFields().add(externalIdField);

				ParsedDocumentField bodyField = new ParsedDocumentField(BODY_FIELD, jsonDoc.getText(), true);
				doc.getDocumentFields().add(bodyField);
				return doc;
			}
		}
		return null;
	}

}
