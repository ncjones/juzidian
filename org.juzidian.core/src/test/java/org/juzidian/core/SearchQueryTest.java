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
import static org.juzidian.core.SearchType.REVERSE;

import org.hamcrest.Description;
import org.hamcrest.Matcher;
import org.hamcrest.TypeSafeMatcher;
import org.junit.Test;

public class SearchQueryTest {

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void constructorShouldRejectNullSearchType() {
		new SearchQuery(null, "foo", 15, 0);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void constructorShouldRejectNullSearchText() {
		new SearchQuery(REVERSE, null, 15, 0);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void constructorShouldRejectNegativePageSize() {
		new SearchQuery(REVERSE, "foo", -1, 0);
	}

	@SuppressWarnings("unused")
	@Test(expected = IllegalArgumentException.class)
	public void constructorShouldRejectNegativePageIndex() {
		new SearchQuery(REVERSE, "foo", 15, -1);
	}

	@Test
	public void nextPageShouldProduceQueryWithPageIndexIncremented() {
		final SearchQuery searchQuery = new SearchQuery(REVERSE, "foo", 15, 0);
		final SearchQuery nextPageQuery = searchQuery.nextPage();
		assertThat(nextPageQuery, query(REVERSE, "foo", 15, 1));
	}

	private static Matcher<SearchQuery> query(final SearchType type, final String text, final int pageSize, final int pageIndex) {
		return new TypeSafeMatcher<SearchQuery>() {

			@Override
			public void describeTo(final Description description) {
				this.describe(description, type, text, pageSize, pageIndex);
			}

			private void describe(final Description description, final SearchType type, final String text, final int pageSize, final int pageIndex) {
				description.appendText("Query[")
				.appendText("type=").appendValue(type)
				.appendText(", text=").appendValue(text)
				.appendText(", pageSize=").appendValue(pageSize)
				.appendText(", pageIndex=").appendValue(pageIndex)
				.appendText("]");
			}

			@Override
			protected void describeMismatchSafely(final SearchQuery item, final Description mismatchDescription) {
				mismatchDescription.appendText("was ");
				this.describe(mismatchDescription, item.getSearchType(), item.getSearchText(), item.getPageSize(), item.getPageIndex());
			}

			@Override
			protected boolean matchesSafely(final SearchQuery item) {
				return is(type).matches(item.getSearchType())
						&& is(text).matches(item.getSearchText())
						&& is(pageSize).matches(item.getPageSize())
						&& is(pageIndex).matches(pageIndex);
			}
		};
	}

}
