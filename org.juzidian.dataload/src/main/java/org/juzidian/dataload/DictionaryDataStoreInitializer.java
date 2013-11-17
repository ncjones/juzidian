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

import javax.inject.Inject;

import org.juzidian.core.DictionaryDataStore;

public class DictionaryDataStoreInitializer {

	private final DictionaryDataStoreEntryPopulator entryPopulator;

	@Inject
	public DictionaryDataStoreInitializer(final DictionaryDataStoreEntryPopulator entryPopulator) {
		this.entryPopulator = entryPopulator;
	}

	public void initializeDb(final DictionaryDataStore dictionaryDataStore) {
		dictionaryDataStore.createSchema();
		dictionaryDataStore.populateMetadata();
		this.entryPopulator.populateEntries(dictionaryDataStore);
	}

}
