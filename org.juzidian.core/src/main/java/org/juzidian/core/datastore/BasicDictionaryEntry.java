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
package org.juzidian.core.datastore;

import java.util.Collections;
import java.util.List;

import org.juzidian.core.DictionaryEntry;
import org.juzidian.core.PinyinSyllable;


/**
 * An immutable {@link DictionaryEntry} which is constructed with its properties
 * directly.
 */
public class BasicDictionaryEntry extends DictionaryEntry {

	private final String traditional;

	private final String simplified;

	private final List<PinyinSyllable> pinyin;

	private final List<String> definitions;

	public BasicDictionaryEntry(final String traditional, final String simplified, final List<PinyinSyllable> pinyin,
			final List<String> definitions) {
		this.traditional = traditional;
		this.simplified = simplified;
		this.pinyin = pinyin;
		this.definitions = definitions;
	}

	@Override
	public String getTraditional() {
		return this.traditional;
	}

	@Override
	public String getSimplified() {
		return this.simplified;
	}

	@Override
	public List<PinyinSyllable> getPinyin() {
		return Collections.unmodifiableList(this.pinyin);
	}

	@Override
	public List<String> getDefinitions() {
		return Collections.unmodifiableList(this.definitions);
	}

}
