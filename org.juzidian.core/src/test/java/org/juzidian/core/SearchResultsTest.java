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

import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.List;

import org.junit.Test;
import org.mockito.Mockito;

public class SearchResultsTest {

	@Test
	public void isLastPageShouldBeTrueWhenEntriesLessThanPageSize() {
		final List<DictionaryEntry> entries = new ArrayList<DictionaryEntry>();
		entries.add(this.createMockEntry());
		entries.add(this.createMockEntry());
		final SearchResults searchResults = new SearchResults(query(3, 0), entries);
		assertThat(searchResults.isLastPage(), is(true));
	}

	private static SearchQuery query(final int pageSize, final int pageIndex) {
		return new SearchQuery(SearchType.PINYIN, "fu", pageSize, pageIndex);
	}

	@Test
	public void isLastPageShouldBeFalseWhenEntriesEqualToPageSize() {
		final List<DictionaryEntry> entries = new ArrayList<DictionaryEntry>();
		entries.add(this.createMockEntry());
		entries.add(this.createMockEntry());
		final SearchResults searchResults = new SearchResults(query(2, 0), entries);
		assertThat(searchResults.isLastPage(), is(false));
	}

	private DictionaryEntry createMockEntry() {
		return Mockito.mock(DictionaryEntry.class);
	}

}
