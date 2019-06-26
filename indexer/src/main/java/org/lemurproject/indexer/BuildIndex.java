package org.lemurproject.indexer;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import org.lemurproject.indexer.domain.IndexingConfiguration;
import org.lemurproject.indexer.factory.IndexOptionsFactory;
import org.lemurproject.indexer.service.IndexService;
import org.lemurproject.indexer.service.LuceneIndexServiceImpl;
import org.xml.sax.SAXException;

public class BuildIndex {

	public static void main(String[] args) throws IOException, InstantiationException, IllegalAccessException,
			IllegalArgumentException, InvocationTargetException, NoSuchMethodException, SecurityException,
			ClassCastException, ClassNotFoundException, SAXException {
		String indexPropertiesFilename = args[0];
		IndexOptionsFactory indexOptionsFactory = new IndexOptionsFactory();
		IndexingConfiguration indexingConfig = indexOptionsFactory.getIndexOptions(indexPropertiesFilename);

		if (!indexingConfig.isIndexFullText()
				&& (indexingConfig.getIndexFields() == null || indexingConfig.getIndexFields().size() == 0)) {
			throw new IllegalArgumentException(
					"Either indexFullText must be true or indexFields must be defined (or both)");
		}

		IndexService indexService = new LuceneIndexServiceImpl();
		indexService.buildIndex(indexingConfig);

	}

}
