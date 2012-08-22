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

import java.util.List;

/**
 * A {@link DefinitionSearchWordCollector} that matches Pinyin.
 */
public class PinyinSearchWordCollector extends SearchWordCollector {

	private final List<PinyinSyllable> pinyinSyllables;

	public PinyinSearchWordCollector(final List<PinyinSyllable> pinyinSyllables) {
		this.pinyinSyllables = pinyinSyllables;
	}

	@Override
	protected boolean matches(final ChineseWord chineseWord) {
		return chineseWord.pinyinStartsWith(this.pinyinSyllables);
	}

	@Override
	protected String getSearchCriteriaDisplay() {
		final StringBuilder stringBuilder = new StringBuilder();
		for (final PinyinSyllable syllable : this.pinyinSyllables) {
			stringBuilder.append(syllable.getDisplayValue()).append(" ");
		}
		return stringBuilder.toString().trim();
	}

}
