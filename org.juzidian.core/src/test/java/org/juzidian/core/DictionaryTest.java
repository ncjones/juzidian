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

import static org.juzidian.core.SearchType.HANZI;
import static org.juzidian.core.SearchType.PINYIN;
import static org.juzidian.core.SearchType.REVERSE;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class DictionaryTest {

	private Dictionary dictionary;

	private DictionaryDataStore dataStore;

	@Before
	public void setUp() {
		this.dataStore = Mockito.mock(DictionaryDataStore.class);
		this.dictionary = new Dictionary(this.dataStore);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findShouldRejectNegativePageSize() {
		this.dictionary.find("han", PINYIN, -5, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findShouldRejectNegativePageIndex() {
		this.dictionary.find("han", PINYIN, 5, -1);
	}

	@Test
	public void findPinyinShouldInvokeDataStoreFindPinyin() {
		this.dictionary.find("han", PINYIN, 5, 0);
		Mockito.verify(this.dataStore).findPinyin(Arrays.asList(new PinyinSyllable("han")), 5, 0);
	}

	@Test
	public void findHanziShouldInvokeDataStoreFindChinese() {
		this.dictionary.find("汉", HANZI, 5, 0);
		Mockito.verify(this.dataStore).findChinese("汉", 5, 0);
	}

	@Test
	public void findReverseShouldInvokeDataStoreFindDefinitions() {
		this.dictionary.find("foo", REVERSE, 5, 0);
		Mockito.verify(this.dataStore).findDefinitions("foo", 5, 0);
	}

	@Test
	public void findPinyinShouldInvokeDataStoreFindPinyinWithPageOffset() {
		this.dictionary.find("han", PINYIN, 5, 2);
		Mockito.verify(this.dataStore).findPinyin(Matchers.<List<PinyinSyllable>> any(), eq(5L), eq(10L));
	}

	@Test
	public void findHanziShouldInvokeDataStoreFindChineseWithPageOffset() {
		this.dictionary.find("汉", HANZI, 5, 2);
		Mockito.verify(this.dataStore).findChinese(anyString(), eq(5L), eq(10L));
	}

	@Test
	public void findReverseShouldInvokeDataStoreFindDefinitionsWithPageOffset() {
		this.dictionary.find("foo", REVERSE, 5, 2);
		Mockito.verify(this.dataStore).findDefinitions(anyString(), eq(5L), eq(10L));
	}

}
