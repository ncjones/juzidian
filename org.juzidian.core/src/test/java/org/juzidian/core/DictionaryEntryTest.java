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

public abstract class DictionaryEntryTest {

	@Test
	public void pinyinStartsWithShouldMatchTonedSyllable() {
		final DictionaryEntry entry = this.createChineseWord(new PinyinSyllable("hao", Tone.THIRD));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("hao", Tone.THIRD));
		assertTrue(entry.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchMultipleTonedSyllables() {
		final DictionaryEntry entry = this.createChineseWord(new PinyinSyllable("ni", Tone.THIRD), new PinyinSyllable("hao", Tone.THIRD));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni", Tone.THIRD), new PinyinSyllable("hao",
				Tone.THIRD));
		assertTrue(entry.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchTonelessSyllable() {
		final DictionaryEntry entry = this.createChineseWord(new PinyinSyllable("hao", Tone.THIRD));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("hao"));
		assertTrue(entry.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchMultipleTonelessSyllables() {
		final DictionaryEntry entry = this.createChineseWord(new PinyinSyllable("ni", Tone.THIRD), new PinyinSyllable("hao", Tone.THIRD));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni"), new PinyinSyllable("hao"));
		assertTrue(entry.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchTonedAndTonelessSyllables() {
		final DictionaryEntry entry = this.createChineseWord(new PinyinSyllable("ni", Tone.THIRD), new PinyinSyllable("hao", Tone.THIRD));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni", Tone.THIRD), new PinyinSyllable("hao"));
		assertTrue(entry.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchTonelessAndTonedSyllables() {
		final DictionaryEntry entry = this.createChineseWord(new PinyinSyllable("ni", Tone.THIRD), new PinyinSyllable("hao", Tone.THIRD));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni"), new PinyinSyllable("hao", Tone.THIRD));
		assertTrue(entry.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchTonedFirstSyllable() {
		final DictionaryEntry entry = this.createChineseWord(new PinyinSyllable("ni", Tone.THIRD), new PinyinSyllable("hao", Tone.THIRD));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni", Tone.THIRD));
		assertTrue(entry.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldMatchTonelessFirstSyllable() {
		final DictionaryEntry entry = this.createChineseWord(new PinyinSyllable("ni", Tone.THIRD), new PinyinSyllable("hao", Tone.THIRD));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni"));
		assertTrue(entry.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldNotMatchIncorrectSyllable() {
		final DictionaryEntry entry = this.createChineseWord(new PinyinSyllable("hao", Tone.THIRD));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("han", Tone.THIRD));
		assertFalse(entry.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldNotMatchIncorrectTone() {
		final DictionaryEntry entry = this.createChineseWord(new PinyinSyllable("hao", Tone.THIRD));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("hao", Tone.FIRST));
		assertFalse(entry.pinyinStartsWith(pinyinSyllables));
	}

	@Test
	public void pinyinStartsWithShouldNotMatchMoreSyllables() {
		final DictionaryEntry entry = this.createChineseWord(new PinyinSyllable("ni", Tone.THIRD), new PinyinSyllable("hao", Tone.THIRD));
		final List<PinyinSyllable> pinyinSyllables = Arrays.asList(new PinyinSyllable("ni"), new PinyinSyllable("hao"),
				new PinyinSyllable("ma"));
		assertFalse(entry.pinyinStartsWith(pinyinSyllables));
	}

	protected abstract DictionaryEntry createChineseWord(final PinyinSyllable... pinyinSyllable);

}
