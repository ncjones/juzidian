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
package org.juzidian.android;

import org.juzidian.core.SearchType;

/**
 * A dictionary search query including search type and text and pagination
 * bounds.
 */
public class SearchQuery {

	private final SearchType searchType;

	private final String searchText;

	private final int pageSize;

	private final int pageIndex;

	public SearchQuery(final SearchType searchType, final String searchText, final int pageSize, final int pageIndex) {
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

	public int getPageSize() {
		return this.pageSize;
	}

	public int getPageIndex() {
		return this.pageIndex;
	}

}
