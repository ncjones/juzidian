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

/**
 * A dictionary search query including search type, search text and pagination
 * bounds.
 */
public class SearchQuery {

	private final SearchType searchType;

	private final String searchText;

	private final int pageSize;

	private final int pageIndex;

	/**
	 * Create a dictionary search query.
	 * 
	 * @param queryString Chinese characters, Pinyin syllables or English words.
	 * @param searchType the {@link SearchType} indicating how to interpret the
	 *        query string.
	 * @param pageSize the number of entries included in each page of results.
	 * @param pageIndex the index of the page of results to get.
	 * @throws IllegalArgumentException if search type or search text are null
	 *         or page size or page number are negative.
	 */
	public SearchQuery(final SearchType searchType, final String searchText, final int pageSize, final int pageIndex) {
		if (searchType == null) {
			throw new IllegalArgumentException("searchType is null");
		}
		if (searchText == null) {
			throw new IllegalArgumentException("searchText is null");
		}
		if (pageSize < 0) {
			throw new IllegalArgumentException("pageSize is negative");
		}
		if (pageIndex < 0) {
			throw new IllegalArgumentException("pageIndex is negative");
		}
		this.searchType = searchType;
		this.searchText = searchText;
		this.pageSize = pageSize;
		this.pageIndex = pageIndex;
	}

	public SearchType getSearchType() {
		return this.searchType;
	}

	public String getSearchText() {
		return this.searchText;
	}

	/**
	 * @return the page size requested.
	 */
	public int getPageSize() {
		return this.pageSize;
	}

	/**
	 * @return the page index requested.
	 */
	public int getPageIndex() {
		return this.pageIndex;
	}

	/**
	 * Get the search query for the next page after this search query.
	 * 
	 * @return a {@link SearchQuery} identical to this one but with the page
	 *         index incremented.
	 */
	public SearchQuery nextPage() {
		return new SearchQuery(this.searchType, this.searchText, this.pageSize, this.pageIndex + 1);
	}

	@Override
	public String toString() {
		return "SearchQuery [searchType=" + this.searchType + ", searchText=" + this.searchText + ", pageSize=" + this.pageSize
				+ ", pageIndex=" + this.pageIndex + "]";
	}

}
