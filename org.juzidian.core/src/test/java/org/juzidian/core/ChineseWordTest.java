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

import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertTrue;

import java.util.Arrays;
import java.util.List;

import org.junit.Test;
import org.juzidian.cedict.CedictEntry;
import org.juzidian.cedict.CedictPinyinSyllable;

public class ChineseWordTest {

	@Test
	public void pinyinStartsWithShouldMatchTonedSyllable() {
		final ChineseWord chineseWord = createChineseWord(new CedictPinyinSyllable("hao", 3));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("hao", Tone.THIRD));
		assertTrue(chineseWord.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchMultipleTonedSyllables() {
		final ChineseWord chineseWord = createChineseWord(new CedictPinyinSyllable("ni", 3), new CedictPinyinSyllable("hao", 3));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni", Tone.THIRD), new PinyinSyllable("hao",
				Tone.THIRD));
		assertTrue(chineseWord.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchTonelessSyllable() {
		final ChineseWord chineseWord = createChineseWord(new CedictPinyinSyllable("hao", 3));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("hao"));
		assertTrue(chineseWord.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchMultipleTonelessSyllables() {
		final ChineseWord chineseWord = createChineseWord(new CedictPinyinSyllable("ni", 3), new CedictPinyinSyllable("hao", 3));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni"), new PinyinSyllable("hao"));
		assertTrue(chineseWord.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchTonedAndTonelessSyllables() {
		final ChineseWord chineseWord = createChineseWord(new CedictPinyinSyllable("ni", 3), new CedictPinyinSyllable("hao", 3));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni", Tone.THIRD), new PinyinSyllable("hao"));
		assertTrue(chineseWord.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchTonelessAndTonedSyllables() {
		final ChineseWord chineseWord = createChineseWord(new CedictPinyinSyllable("ni", 3), new CedictPinyinSyllable("hao", 3));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni"), new PinyinSyllable("hao", Tone.THIRD));
		assertTrue(chineseWord.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchTonedFirstSyllable() {
		final ChineseWord chineseWord = createChineseWord(new CedictPinyinSyllable("ni", 3), new CedictPinyinSyllable("hao", 3));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni", Tone.THIRD));
		assertTrue(chineseWord.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchTonelessFirstSyllable() {
		final ChineseWord chineseWord = createChineseWord(new CedictPinyinSyllable("ni", 3), new CedictPinyinSyllable("hao", 3));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni"));
		assertTrue(chineseWord.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldNotMatchIncorrectSyllable() {
		final ChineseWord chineseWord = createChineseWord(new CedictPinyinSyllable("hao", 3));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("han", Tone.THIRD));
		assertFalse(chineseWord.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldNotMatchIncorrectTone() {
		final ChineseWord chineseWord = createChineseWord(new CedictPinyinSyllable("hao", 3));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("hao", Tone.FIRST));
		assertFalse(chineseWord.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldNotMatchMoreSyllables() {
		final ChineseWord chineseWord = createChineseWord(new CedictPinyinSyllable("ni", 3), new CedictPinyinSyllable("hao", 3));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni"), new PinyinSyllable("hao"),
				new PinyinSyllable("ma"));
		assertFalse(chineseWord.pinyinStartsWith(pinyinSyllables));
	}

	private ChineseWord createChineseWord(final CedictPinyinSyllable... cedictPinyinSyllable) {
		return new ChineseWord(createMockCedictEntry(cedictPinyinSyllable));
	}

	private CedictEntry createMockCedictEntry(final CedictPinyinSyllable... pinyinSyllables) {
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
				return Arrays.asList(pinyinSyllables);
			}

			@Override
			public List<String> getDefinitions() {
				return null;
			}
		};
	}
}
