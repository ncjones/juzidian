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

import java.io.InputStream;

import org.juzidian.cedict.CedictInputStreamProvider;
import org.juzidian.cedict.CedictLoader;

public class StreamingDictionaryFactory implements DictionaryFactory {

	@Override
	public StreamingDictionary createDictionary() {
		final StreamingDictionary dictionary = new StreamingDictionary(new CedictLoader(new CedictInputStreamProvider() {
			@Override
			public InputStream getInputStream() {
				return this.getClass().getResourceAsStream("/cedict-data.txt");
			}
		}));
		return dictionary;
	}

}
