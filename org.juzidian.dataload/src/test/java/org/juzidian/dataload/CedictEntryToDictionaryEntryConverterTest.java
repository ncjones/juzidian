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

import static org.hamcrest.Matchers.contains;
import static org.hamcrest.Matchers.hasSize;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.juzidian.cedict.CedictEntry;
import org.juzidian.cedict.CedictPinyinSyllable;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.pinyin.PinyinSyllable;
import org.juzidian.pinyin.Tone;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

public class CedictEntryToDictionaryEntryConverterTest {

	@Mock
	private CedictEntry cedictEntry;

	private CedictEntryToDictionaryEntryConverter converter;

	@Before
	public void setUp() {
		MockitoAnnotations.initMocks(this);
		converter = new CedictEntryToDictionaryEntryConverter();
	}

	private static CedictPinyinSyllable newCedictPinyin(final String letters, final int tone) {
		return new CedictPinyinSyllable(letters, tone);
	}

	@Test
	public void convertShouldCopyDefinitions() {
		when(cedictEntry.getDefinitions()).thenReturn(Arrays.asList("One", "Two"));
		assertThat(converter.convert(cedictEntry).getDefinitions(), contains("One", "Two"));
	}

	@Test
	public void convertShouldCopyTraditional() {
		when(cedictEntry.getTraditionalCharacters()).thenReturn("個");
		assertThat(converter.convert(cedictEntry).getTraditional(), is("個"));
	}

	@Test
	public void convertShouldCopySimplified() {
		when(cedictEntry.getTraditionalCharacters()).thenReturn("个");
		assertThat(converter.convert(cedictEntry).getTraditional(), is("个"));
	}

	@Test
	public void convertShouldCopyPinyinSyllables() {
		when(cedictEntry.getPinyinSyllables()).thenReturn(Arrays.asList(newCedictPinyin("Guang", 2), newCedictPinyin("zhou", 1)));
		final DictionaryEntry dictionaryEntry = converter.convert(cedictEntry);
		List<PinyinSyllable> pinyin = dictionaryEntry.getPinyin();
		assertThat(pinyin, hasSize(2));
		assertThat(pinyin.get(0).getLetters(), is("Guang"));
		assertThat(pinyin.get(0).getTone(), is(Tone.SECOND));
		assertThat(pinyin.get(1).getLetters(), is("zhou"));
		assertThat(pinyin.get(1).getTone(), is(Tone.FIRST));
	}

	@Test
	public void convertShouldSubstituteUmlautInPinyin() {
		when(cedictEntry.getPinyinSyllables()).thenReturn(Arrays.asList(newCedictPinyin("yu:", 3)));
		final DictionaryEntry dictionaryEntry = converter.convert(cedictEntry);
		final PinyinSyllable pinyinSyllable = dictionaryEntry.getPinyin().get(0);
		assertThat(pinyinSyllable.getLetters(), is("yü"));
	}

}
