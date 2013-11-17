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
package org.juzidian.dataload;

import java.io.IOException;

import javax.inject.Inject;

import org.juzidian.cedict.CedictLoader;
import org.juzidian.core.DictionaryDataStore;

/**
 * Populates entries into a {@link DictionaryDataStore} from a
 * {@link CedictLoader}.
 */
public class DictionaryDataStoreEntryPopulator {

	private final CedictLoader cedictLoader;

	private final CedictEntryToDictionaryEntryConverter entryConverter;

	@Inject
	public DictionaryDataStoreEntryPopulator(final CedictLoader cedictLoader, final CedictEntryToDictionaryEntryConverter entryConverter) {
		this.cedictLoader = cedictLoader;
		this.entryConverter = entryConverter;
	}

	public void populateEntries(final DictionaryDataStore dictionaryDataStore) {
		final EntryCollector entryCollector = new EntryCollector(entryConverter);
		try {
			this.cedictLoader.loadEntries(entryCollector);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load entries", e);
		}
		dictionaryDataStore.add(entryCollector.getEntries());
	}

}
