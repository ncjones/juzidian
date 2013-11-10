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

import java.util.Collection;
import java.util.LinkedList;
import java.util.List;

import javax.inject.Inject;

import org.juzidian.cedict.CedictEntry;
import org.juzidian.cedict.CedictLoadHandler;
import org.juzidian.core.DictionaryEntry;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class EntryCollector implements CedictLoadHandler {

	private static final Logger LOGGER = LoggerFactory.getLogger(EntryCollector.class);

	private final CedictEntryToDictionaryEntryConverter entryConverter;

	private final List<DictionaryEntry> entries = new LinkedList<DictionaryEntry>();

	@Inject
	public EntryCollector(final CedictEntryToDictionaryEntryConverter entryConverter) {
		this.entryConverter = entryConverter;
	}

	public Collection<DictionaryEntry> getEntries() {
		return this.entries;
	}

	@Override
	public void loadingStarted() {

	}

	@Override
	public void entryLoaded(final CedictEntry cedictEntry) {
		this.entries.add(entryConverter.convert(cedictEntry));
	}

	@Override
	public void loadingFinished() {
		LOGGER.info("Loaded {} entries from CEDict data.", this.entries.size());
	}

}