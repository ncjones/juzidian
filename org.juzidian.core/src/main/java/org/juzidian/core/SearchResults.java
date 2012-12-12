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

import java.util.Collections;
import java.util.List;

/**
 * A page of results from a dictionary search.
 */
public class SearchResults {

	private final int pageSize;

	private final List<DictionaryEntry> entries;

	SearchResults(final int pageSize, final List<DictionaryEntry> entries) {
		this.pageSize = pageSize;
		this.entries = entries;
	}

	/**
	 * @return the list of {@link DictionaryEntry} that match the search
	 *         criteria and page bounds.
	 */
	public List<DictionaryEntry> getEntries() {
		return Collections.unmodifiableList(this.entries);
	}

	/**
	 * @return the page size requested in the search query.
	 */
	public int getPageSize() {
		return this.pageSize;
	}

}
