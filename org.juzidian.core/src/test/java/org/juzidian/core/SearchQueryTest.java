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

import static org.juzidian.core.SearchType.REVERSE;

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

}
