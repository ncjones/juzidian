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

import java.io.IOException;
import java.text.MessageFormat;
import java.util.List;

import org.juzidian.core.Dictionary;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.core.InMemoryDictionaryFactory;
import org.juzidian.core.SearchType;

/**
 * A basic command-line interface for performing dictionary searches.
 */
public class JuzidianCli {

	public static void main(final String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("Search type must be specified.");
			return;
		}
		if (args.length < 2) {
			System.out.println("Search query must be specified.");
			return;
		}
		final SearchType searchType = SearchType.valueOf(args[0]);
		final Dictionary dictionary = new InMemoryDictionaryFactory().createDictionary();
		final Runtime runtime = Runtime.getRuntime();
		final long totalMemory = runtime.totalMemory();
		final long freeMemory = runtime.freeMemory();
		System.out.println(MessageFormat.format("Memory used: {0}KB", (totalMemory - freeMemory) / 1024));
		final String queryString = args[1];
		final long start = System.nanoTime();
		final List<DictionaryEntry> foundCharacters = dictionary.find(queryString, searchType);
		final long end = System.nanoTime();
		printSearchResults(queryString, foundCharacters, end - start);
	}

	private static void printSearchResults(final String query, final List<DictionaryEntry> entries, final long searchDuration) {
		System.out.println(String.format("Found %d words matching '%s' in %.3f seconds:", entries.size(), query,
				searchDuration / 1000 / 1000 / 1000f));
		for (final DictionaryEntry entry : entries) {
			System.out.println(entry);
		}
	}
}
