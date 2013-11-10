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
package org.juzidian.core.datastore;

import java.util.Arrays;

import org.juzidian.cedict.CedictPinyinSyllable;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.core.DictionaryEntryTest;
import org.juzidian.pinyin.PinyinSyllable;

public class BasicDictionaryEntryTest extends DictionaryEntryTest {

	@Override
	protected DictionaryEntry createChineseWord(final PinyinSyllable... pinyinSyllables) {
		final CedictPinyinSyllable[] cedictPinyinSyllables = new CedictPinyinSyllable[pinyinSyllables.length];
		for (int i = 0; i < pinyinSyllables.length; i++) {
			final PinyinSyllable pinyinSyllable = pinyinSyllables[i];
			final CedictPinyinSyllable cedictPinyinSyllable = new CedictPinyinSyllable(pinyinSyllable.getLetters(), pinyinSyllable
					.getTone().getNumber());
			cedictPinyinSyllables[i] = cedictPinyinSyllable;
		}
		return new BasicDictionaryEntry(null, null, Arrays.asList(pinyinSyllables), null);
	}

}
