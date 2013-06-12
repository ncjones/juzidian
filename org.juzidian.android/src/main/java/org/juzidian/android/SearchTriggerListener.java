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
 * Listener which is notified when a search is triggered.
 */
public interface SearchTriggerListener {

	/**
	 * A search has been triggered.
	 * 
	 * @param searchType the type of search triggered. May be {@code null} for
	 *        an empty search.
	 * @param searchText the text to search for.
	 */
	void searchTriggered(SearchType searchType, String searchText);

}
