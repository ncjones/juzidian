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

package org.juzidian.core;

import java.io.IOException;
import java.text.MessageFormat;

import javax.inject.Inject;

import org.juzidian.cedict.CedictLoader;

public class InMemoryDictionaryFactory implements DictionaryFactory {

	private final CedictLoader cedictLoader;

	@Inject
	public InMemoryDictionaryFactory(final CedictLoader cedictLoader) {
		this.cedictLoader = cedictLoader;
	}

	@Override
	public InMemoryDictionary createDictionary() {
		final InMemoryDictionary dictionary = new InMemoryDictionary();
		final InMemoryDictionaryLoadHandler handler = new InMemoryDictionaryLoadHandler(dictionary);
		try {
			this.cedictLoader.loadEntries(handler);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load cedict entries.", e);
		}
		System.out.println(MessageFormat.format("Loaded {0} entries in {1, number, #.###} seconds", handler.getEntryCount(),
				handler.getDuration() / 1000 / 1000 / 1000d));
		return dictionary;
	}

}
