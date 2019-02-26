/*
 * ===============================================================================================
 * Copyright (c) 2016 Carnegie Mellon University and University of Massachusetts. All Rights
 * Reserved.
 *
 * Use of the Lemur Toolkit for Language Modeling and Information Retrieval is subject to the terms
 * of the software license set forth in the LICENSE file included with this software, and also
 * available at http://www.lemurproject.org/license.html
 *
 * ================================================================================================
 */
package org.lemurproject.indexer.factory;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import org.lemurproject.indexer.domain.IndexingConfiguration;

/**
 * Defines the IndexOptions object based on the user input properties file.
 * 
 * @author cmw2
 *
 *         Nov 30, 2016
 */
public class IndexOptionsFactory {

	public IndexingConfiguration getIndexOptions(String propertiesFileName) throws IOException {
		Properties properties = readPropertiesFromFile(propertiesFileName);
		IndexingConfiguration options = new IndexingConfiguration();
		options.setDocumentFormat(properties.getProperty("documentFormat"));
		options.setDataDirectory(properties.getProperty("dataDirectory"));
		options.setIndexDirectory(properties.getProperty("indexDirectory"));
		options.setIndexName(properties.getProperty("indexName"));
		options.setStemmer(properties.getProperty("stemmer"));
		options.setRemoveStopwords(Boolean.valueOf(properties.getProperty("removeStopwords")));
		options.setIgnoreCase(Boolean.valueOf(properties.getProperty("ignoreCase")));
		return options;
	}

	private Properties readPropertiesFromFile(String propertiesFileName) throws IOException {
		Properties properties = new Properties();

		File propertiesFile = new File(propertiesFileName);
		InputStream is = new FileInputStream(propertiesFile);
		properties.load(is);

		return properties;
	}

}
