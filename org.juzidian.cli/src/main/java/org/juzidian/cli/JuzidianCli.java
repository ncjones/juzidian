/*
 * Copyright Nathan Jones 2012
 * 
 * This file is part of Juzidian.
 *
 * Juzidian is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Juzidian is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Juzidian.  If not, see <http://www.gnu.org/licenses/>.
 */
package org.juzidian.cli;

import java.io.File;
import java.io.FileOutputStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.juzidian.core.Dictionary;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.core.SearchQuery;
import org.juzidian.core.SearchType;
import org.juzidian.core.dataload.DictionaryResource;
import org.juzidian.core.dataload.DictionaryResourceDownloader;
import org.juzidian.core.dataload.DictionaryResourceDownloaderException;
import org.juzidian.core.dataload.DictionaryResourceRegistry;
import org.juzidian.core.dataload.DictionaryResourceRegistryService;
import org.juzidian.core.dataload.DictonaryResourceRegistryServiceException;
import org.juzidian.core.datastore.DbDictionaryDataStore;
import org.juzidian.core.inject.DictionaryModule;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.inject.Guice;
import com.google.inject.Injector;
import com.j256.ormlite.jdbc.JdbcConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

/**
 * A basic command-line interface for performing dictionary searches.
 */
public class JuzidianCli {

	private static final int PAGE_SIZE = 1000;

	private static final Logger LOGGER = LoggerFactory.getLogger(JuzidianCli.class);

	private static final File DICTIONARY_DB_FILE = new File(System.getProperty("user.home") + "/.juzidian/juzidian-dictionary.db");

	private static Injector INJECTOR = Guice.createInjector(new DictionaryModule() {

		@Override
		protected ConnectionSource createConnectionSource() throws SQLException {
			return new JdbcConnectionSource("jdbc:sqlite:" + DICTIONARY_DB_FILE.getAbsolutePath());
		}
	});

	public static void main(final String[] args) throws Exception {
		if (args.length < 1) {
			System.out.println("Search type must be specified.");
			return;
		}
		if (args.length < 2) {
			System.out.println("Search query must be specified.");
			return;
		}
		final SearchType searchType = SearchType.valueOf(args[1]);
		initializeDb();
		final Dictionary dictionary = INJECTOR.getInstance(Dictionary.class);
		final Runtime runtime = Runtime.getRuntime();
		final long totalMemory = runtime.totalMemory();
		final long freeMemory = runtime.freeMemory();
		LOGGER.debug(MessageFormat.format("Memory used: {0}KB", (totalMemory - freeMemory) / 1024));
		final String queryString = args[2];
		final List<DictionaryEntry> foundCharacters = findAllWords(dictionary, queryString, searchType);
		printSearchResults(foundCharacters);
	}

	private static void initializeDb() throws Exception {
		if (!DICTIONARY_DB_FILE.exists()) {
			LOGGER.info("Dictionary DB missing.");
			downloadDb(true);
		} else {
			final DbDictionaryDataStore dataStore = INJECTOR.getInstance(DbDictionaryDataStore.class);
			if (dataStore.getCurrentDataFormatVersion() != DbDictionaryDataStore.DATA_FORMAT_VERSION) {
				LOGGER.info("Dictionary DB incompatible.");
				downloadDb(false);
			} else {
				LOGGER.debug("Dictionary DB compatible.");
			}
		}
	}

	private static void downloadDb(final boolean createNewFile) throws Exception {
		DICTIONARY_DB_FILE.getParentFile().mkdirs();
		final DictionaryResource resource = getDictionaryResource();
		final DictionaryResourceDownloader downloader = INJECTOR.getInstance(DictionaryResourceDownloader.class);
		LOGGER.info("Downloading dictionary DB: " + resource.getUrl());
		try {
			downloader.download(resource, new FileOutputStream(DICTIONARY_DB_FILE), new DownloadProgressLogger());
		} catch (final DictionaryResourceDownloaderException e) {
			if (createNewFile) {
				/* make sure the newly created (empty) file is removed */
				DICTIONARY_DB_FILE.delete();
			}
			throw e;
		}
	}

	private static DictionaryResource getDictionaryResource() throws DictonaryResourceRegistryServiceException {
		final DictionaryResourceRegistry registry = getDictionaryResourceRegistry();
		return registry.getDictionaryResources().get(0);
	}

	private static DictionaryResourceRegistry getDictionaryResourceRegistry() throws DictonaryResourceRegistryServiceException {
		final DictionaryResourceRegistryService registryService = INJECTOR.getInstance(DictionaryResourceRegistryService.class);
		LOGGER.debug("Getting registry of available dictionary DBs.");
		final DictionaryResourceRegistry registry = registryService.getDictionaryResourceRegistry(DbDictionaryDataStore.DATA_FORMAT_VERSION);
		return registry;
	}

	private static List<DictionaryEntry> findAllWords(final Dictionary dictionary, final String queryString, final SearchType searchType) {
		final List<DictionaryEntry> allEntries = new ArrayList<DictionaryEntry>();
		int pageNumber = 0;
		List<DictionaryEntry> entries;
		do {
			entries = dictionary.find(new SearchQuery(searchType, queryString, PAGE_SIZE, pageNumber)).getEntries();
			allEntries.addAll(entries);
			pageNumber += 1;
		} while (entries.size() == PAGE_SIZE);
		return allEntries;
	}

	private static void printSearchResults(final List<DictionaryEntry> entries) {
		for (final DictionaryEntry entry : entries) {
			System.out.println(entry);
		}
	}
}
