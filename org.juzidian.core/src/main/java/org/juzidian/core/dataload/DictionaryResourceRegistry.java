/*
 * Copyright Nathan Jones 2013
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
package org.juzidian.core.dataload;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * A registry of available {@link DictionaryResource}s.
 */
public class DictionaryResourceRegistry {

	private final List<DictionaryResource> dictionaryResources;

	public DictionaryResourceRegistry(final List<DictionaryResource> dictionaryResources) {
		this.dictionaryResources = new ArrayList<DictionaryResource>(dictionaryResources);
	}

	/**
	 * @return the available dictionary resources.
	 */
	public List<DictionaryResource> getDictionaryResources() {
		return Collections.unmodifiableList(this.dictionaryResources);
	}

}
