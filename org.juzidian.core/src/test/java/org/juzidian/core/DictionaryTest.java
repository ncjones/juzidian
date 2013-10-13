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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.juzidian.core.SearchType.HANZI;
import static org.juzidian.core.SearchType.PINYIN;
import static org.juzidian.core.SearchType.REVERSE;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.eq;
import static org.mockito.Matchers.isA;
import static org.mockito.Matchers.isNull;
import static org.mockito.Mockito.verify;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.juzidian.core.SearchCanceller.Listener;
import org.juzidian.pinyin.PinyinParser;
import org.juzidian.pinyin.PinyinSyllable;
import org.mockito.ArgumentCaptor;
import org.mockito.Matchers;
import org.mockito.Mockito;

public class DictionaryTest {

	private Dictionary dictionary;

	private DictionaryDataStore dataStore;

	@Before
	public void setUp() {
		this.dataStore = Mockito.mock(DictionaryDataStore.class);
		this.dictionary = new Dictionary(this.dataStore, new PinyinParser(), new CurrentThreadExecutor());
	}

	private static List<PinyinSyllable> pinyinSyllables(final String syllable) {
		return Arrays.asList(new PinyinSyllable(syllable));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findShouldRejectNegativePageSize() {
		this.dictionary.find(new SearchQuery(PINYIN, "han", -5, 0));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findShouldRejectNegativePageIndex() {
		this.dictionary.find(new SearchQuery(PINYIN, "han", 5, -1));
	}

	@Test
	public void findPinyinShouldInvokeDataStoreFindPinyin() {
		this.dictionary.find(new SearchQuery(PINYIN, "han", 5, 0));
		verify(this.dataStore).findPinyin(pinyinSyllables("han"), 5, 0, null);
	}

	@Test
	public void findHanziShouldInvokeDataStoreFindChinese() {
		this.dictionary.find(new SearchQuery(HANZI, "汉", 5, 0));
		verify(this.dataStore).findChinese("汉", 5, 0, null);
	}

	@Test
	public void findReverseShouldInvokeDataStoreFindDefinitions() {
		this.dictionary.find(new SearchQuery(REVERSE, "foo", 5, 0));
		verify(this.dataStore).findDefinitions("foo", 5, 0, null);
	}

	@Test
	public void findPinyinShouldInvokeDataStoreFindPinyinWithPageOffset() {
		this.dictionary.find(new SearchQuery(PINYIN, "han", 5, 2));
		verify(this.dataStore).findPinyin(Matchers.<List<PinyinSyllable>> any(), eq(5L), eq(10L), (SearchCanceller) isNull());
	}

	@Test
	public void findHanziShouldInvokeDataStoreFindChineseWithPageOffset() {
		this.dictionary.find(new SearchQuery(HANZI, "汉", 5, 2));
		verify(this.dataStore).findChinese(anyString(), eq(5L), eq(10L), (SearchCanceller) isNull());
	}

	@Test
	public void findReverseShouldInvokeDataStoreFindDefinitionsWithPageOffset() {
		this.dictionary.find(new SearchQuery(REVERSE, "foo", 5, 2));
		verify(this.dataStore).findDefinitions(anyString(), eq(5L), eq(10L), (SearchCanceller) isNull());
	}

	@Test
	public void findPinyinShouldChangeVToUmlaut() {
		this.dictionary.find(new SearchQuery(PINYIN, "nv", 5, 0));
		final List<PinyinSyllable> pinyin = pinyinSyllables("nü");
		verify(this.dataStore).findPinyin(eq(pinyin), anyLong(), anyLong(), (SearchCanceller) isNull());
	}

	@Test
	public void findShouldReturnSearchResultsWithDataStoreEntries() {
		final List<DictionaryEntry> entries = Arrays.asList(Mockito.mock(DictionaryEntry.class), Mockito.mock(DictionaryEntry.class));
		Mockito.when(this.dataStore.findDefinitions(anyString(), anyLong(), anyLong(), (SearchCanceller) isNull())).thenReturn(entries);
		final SearchQuery query = new SearchQuery(REVERSE, "foo", 20, 0);
		final SearchResults searchResults = this.dictionary.find(query);
		assertThat(searchResults.getEntries(), is(equalTo(entries)));
	}

	@Test
	public void findShouldReturnSearchResultsWithGivenQuery() {
		final SearchQuery query = new SearchQuery(REVERSE, "foo", 20, 0);
		final SearchResults searchResults = this.dictionary.find(query);
		assertThat(searchResults.getSearchQuery(), is(query));
	}

	@Test
	public void findAsyncPinyinShouldInvokeDataStoreFindPinyin() {
		this.dictionary.findAsync(new SearchQuery(PINYIN, "han", 5, 0));
		verify(this.dataStore).findPinyin(eq(pinyinSyllables("han")), eq(5L), eq(0L), isA(SearchCanceller.class));
	}

	@Test
	public void findAsyncHanziShouldInvokeDataStoreFindChinese() {
		this.dictionary.findAsync(new SearchQuery(HANZI, "汉", 5, 0));
		verify(this.dataStore).findChinese(eq("汉"), eq(5L), eq(0L), isA(SearchCanceller.class));
	}

	@Test
	public void findAsyncReverseShouldInvokeDataStoreFindDefinitions() {
		this.dictionary.findAsync(new SearchQuery(REVERSE, "foo", 5, 0));
		verify(this.dataStore).findDefinitions(eq("foo"), eq(5L), eq(0L), isA(SearchCanceller.class));
	}

	@Test
	public void findAsyncShouldReturnSearchResultsFutureWhichProvidesSearchResultsWithEntries() throws Exception {
		final List<DictionaryEntry> entries = Arrays.asList(Mockito.mock(DictionaryEntry.class), Mockito.mock(DictionaryEntry.class));
		Mockito.when(this.dataStore.findDefinitions(anyString(), anyLong(), anyLong(), isA(SearchCanceller.class))).thenReturn(entries);
		final SearchQuery query = new SearchQuery(REVERSE, "foo", 20, 0);
		final SearchResultsFuture searchResultsFuture = this.dictionary.findAsync(query);
		assertThat(searchResultsFuture.getResults().getEntries(), is(equalTo(entries)));
	}

	@Test
	public void findAsyncShouldReturnSearchResultsFutureWhichIsBoundToCanceller() throws Exception {
		final Listener mockListener = Mockito.mock(Listener.class);
		final ArgumentCaptor<SearchCanceller> cancellerCaptor = ArgumentCaptor.forClass(SearchCanceller.class);
		final SearchQuery query = new SearchQuery(REVERSE, "foo", 20, 0);
		final SearchResultsFuture searchResultsFuture = this.dictionary.findAsync(query);
		verify(this.dataStore).findDefinitions(anyString(), anyLong(), anyLong(), cancellerCaptor.capture());
		final SearchCanceller canceller = cancellerCaptor.getValue();
		canceller.register(mockListener);
		searchResultsFuture.cancel();
		verify(mockListener).onCancel();
	}

}
