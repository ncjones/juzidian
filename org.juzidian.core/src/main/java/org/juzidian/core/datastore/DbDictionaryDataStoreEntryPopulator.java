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
package org.juzidian.core.datastore;

import java.io.IOException;

import javax.inject.Inject;

import org.juzidian.cedict.CedictEntry;
import org.juzidian.cedict.CedictLoadHandler;
import org.juzidian.cedict.CedictLoader;
import org.juzidian.core.DictionaryDataStore;
import org.juzidian.core.DictionaryEntry;

/**
 * Populates entries into a {@link DictionaryDataStore} from a
 * {@link CedictLoader}.
 */
public class DbDictionaryDataStoreEntryPopulator {

	private final CedictLoader cedictLoader;

	@Inject
	public DbDictionaryDataStoreEntryPopulator(final CedictLoader cedictLoader) {
		this.cedictLoader = cedictLoader;
	}

	public void populateEntries(final DictionaryDataStore dictionaryDataStore) {
		try {
			this.cedictLoader.loadEntries(new DataLoadHandler(dictionaryDataStore));
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load entries", e);
		}
	}

	private static class DataLoadHandler implements CedictLoadHandler {

		private final DictionaryDataStore dictionaryDataStore;

		public DataLoadHandler(final DictionaryDataStore dictionaryDataStore) {
			this.dictionaryDataStore = dictionaryDataStore;
		}

		@Override
		public void loadingStarted() {

		}

		@Override
		public void entryLoaded(final CedictEntry cedictEntry) {
			final DictionaryEntry entry = new CedictDictionaryEntryAdaptor(cedictEntry);
			this.dictionaryDataStore.add(entry);
		}

		@Override
		public void loadingFinished() {

		}

	}

}
