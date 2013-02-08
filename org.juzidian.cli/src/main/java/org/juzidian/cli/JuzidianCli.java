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
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.sql.SQLException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import org.juzidian.core.Dictionary;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.core.SearchQuery;
import org.juzidian.core.SearchType;
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

	private static final String DICTIONARY_DB_FILENAME = "juzidian_dictionary.db";

	public static void main(final String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Search type must be specified.");
			return;
		}
		if (args.length < 2) {
			System.out.println("Search query must be specified.");
			return;
		}
		initializeDbFile();
		final SearchType searchType = SearchType.valueOf(args[0]);
		final Injector injector = Guice.createInjector(new DictionaryModule() {
			@Override
			protected ConnectionSource createConnectionSource() throws SQLException {
				return new JdbcConnectionSource("jdbc:sqlite:" + DICTIONARY_DB_FILENAME);
			}
		});
		final Dictionary dictionary = injector.getInstance(Dictionary.class);
		final Runtime runtime = Runtime.getRuntime();
		final long totalMemory = runtime.totalMemory();
		final long freeMemory = runtime.freeMemory();
		LOGGER.debug(MessageFormat.format("Memory used: {0}KB", (totalMemory - freeMemory) / 1024));
		final String queryString = args[1];
		final List<DictionaryEntry> foundCharacters = findAllWords(dictionary, queryString, searchType);
		printSearchResults(foundCharacters);
	}

	private static void initializeDbFile() throws IOException {
		final InputStream inputStream = JuzidianCli.class.getResourceAsStream("/" + DICTIONARY_DB_FILENAME);
		final File dbFile = new File(DICTIONARY_DB_FILENAME);
		dbFile.delete();
		dbFile.createNewFile();
		copy(inputStream, new FileOutputStream(dbFile));
	}

	public static void copy(final InputStream in, final OutputStream out) throws IOException {
		final byte[] buf = new byte[10000];
		int len;
		while ((len = in.read(buf)) > 0) {
			out.write(buf, 0, len);
		}
		in.close();
		out.close();
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
