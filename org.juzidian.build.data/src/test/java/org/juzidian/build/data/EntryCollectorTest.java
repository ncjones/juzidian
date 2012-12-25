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
package org.juzidian.build.data;

import java.util.Collections;
import java.util.List;

import org.hamcrest.Matchers;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.juzidian.cedict.CedictEntry;
import org.juzidian.cedict.CedictPinyinSyllable;

public class EntryCollectorTest {

	private EntryCollector entryCollector;

	@Before
	public void setUp() {
		this.entryCollector = new EntryCollector();
	}

	@Test
	public void entryLoadedShouldDiscardInvalidPinyinSyllable() {
		final CedictEntry cedictEntry = createSingleSyllableCedictEntry("m", 5);
		this.entryCollector.entryLoaded(cedictEntry);
		Assert.assertThat(this.entryCollector.getEntries(), Matchers.hasSize(0));
	}

	@Test
	public void entryLoadedShouldKeepValidPinyinSyllable() {
		final CedictEntry cedictEntry = createSingleSyllableCedictEntry("wu", 5);
		this.entryCollector.entryLoaded(cedictEntry);
		Assert.assertThat(this.entryCollector.getEntries(), Matchers.hasSize(1));
	}

	private static CedictEntry createSingleSyllableCedictEntry(final String pinyinLetters, final int toneNumber) {
		return new CedictEntry() {

			@Override
			public String getTraditionalCharacters() {
				return null;
			}

			@Override
			public String getSimplifiedCharacters() {
				return null;
			}

			@Override
			public List<CedictPinyinSyllable> getPinyinSyllables() {
				return Collections.singletonList(new CedictPinyinSyllable(pinyinLetters, toneNumber));
			}

			@Override
			public List<String> getDefinitions() {
				return null;
			}
		};
	}

}
