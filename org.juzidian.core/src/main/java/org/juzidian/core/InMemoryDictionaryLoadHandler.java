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

import org.juzidian.cedict.CedictEntry;
import org.juzidian.cedict.CedictLoadHandler;

/**
 * A {@link CedictLoadHandler} which populates a
 * {@link InMemoryDictionary}.
 */
public class InMemoryDictionaryLoadHandler implements CedictLoadHandler {

	private final InMemoryDictionary dictionary;

	private int entryCount = 0;

	private long startNanoTime;

	private long finishNanoTime;

	public InMemoryDictionaryLoadHandler(final InMemoryDictionary dictionary) {
		this.dictionary = dictionary;
	}

	@Override
	public void entryLoaded(final CedictEntry cedictEntry) {
		this.dictionary.addWord(new DictionaryEntry(cedictEntry));
		this.entryCount += 1;
	}

	@Override
	public void loadingStarted() {
		this.startNanoTime = System.nanoTime();
	}

	@Override
	public void loadingFinished() {
		this.finishNanoTime = System.nanoTime();
	}

	public int getEntryCount() {
		return this.entryCount;
	}

	public long getDuration() {
		return this.finishNanoTime - this.startNanoTime;
	}

}
