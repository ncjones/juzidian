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
 * A {@link DefinitionSearchWordCollector} that matches word definitions.
 */
class DefinitionSearchWordCollector extends SearchWordCollector {

	private final String searchQuery;

	public DefinitionSearchWordCollector(final String searchQuery) {
		this.searchQuery = searchQuery;
	}

	@Override
	protected boolean matches(final DictionaryEntry chineseWord) {
		return chineseWord.getDefinitions().contains(this.searchQuery);
	}

	@Override
	protected String getSearchCriteriaDisplay() {
		return this.searchQuery;
	}

}
