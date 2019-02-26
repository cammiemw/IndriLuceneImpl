package org.lemurproject.indexer.documentparser;

import java.io.IOException;

import org.lemurproject.indexer.domain.ParsedDocument;
import org.xml.sax.SAXException;

public interface DocumentParser {
	
	/**
	   * 
	   * @return boolean defining whether another document exists
	   */
	  boolean hasNextDocument();

	  /**
	   * Examines input to find the next document and split it into fields.
	   * 
	   * @return
	   * @throws IOException
	   */
	  ParsedDocument getNextDocument() throws IOException, SAXException;

}
