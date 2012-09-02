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
import java.io.InputStream;
import java.util.List;

import org.juzidian.cedict.CedictLoader;
import org.juzidian.core.ChineseEnglishDictionary;
import org.juzidian.core.ChineseWord;
import org.juzidian.core.SearchType;
import org.juzidian.core.SimpleChineseEnglishDictionary;
import org.juzidian.core.SimpleChineseEnglishDictionaryLoadHandler;

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
		final ChineseEnglishDictionary dictionary = createDictionary();

		final String queryString = args[1];
		final long start = System.nanoTime();
		final List<ChineseWord> foundCharacters = dictionary.find(queryString, searchType);
		final long end = System.nanoTime();
		printSearchResults(queryString, foundCharacters, end - start);
	}

	private static ChineseEnglishDictionary createDictionary() throws IOException {
		final SimpleChineseEnglishDictionary dictionary = new SimpleChineseEnglishDictionary();
		final SimpleChineseEnglishDictionaryLoadHandler handler = new SimpleChineseEnglishDictionaryLoadHandler(dictionary);
		final CedictLoader cedictFileLoader = new CedictLoader();
		final InputStream inputStream = JuzidianCli.class.getResourceAsStream("/cedict-data.txt");
		cedictFileLoader.loadEntries(inputStream, handler);
		System.out.println(String.format("Loaded %d entries in %.3f seconds", handler.getEntryCount(),
				handler.getDuration() / 1000 / 1000 / 1000d));
		return dictionary;
	}

	private static void printSearchResults(final String query, final List<ChineseWord> words, final long searchDuration) {
		System.out.println(String.format("Found %d words matching '%s' in %.3f seconds:", words.size(), query,
				searchDuration / 1000 / 1000 / 1000f));
		for (final ChineseWord chineseWord : words) {
			System.out.println(chineseWord);
		}
	}
}
