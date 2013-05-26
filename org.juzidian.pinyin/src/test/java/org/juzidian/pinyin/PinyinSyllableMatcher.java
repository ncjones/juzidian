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
package org.juzidian.pinyin;

import static org.hamcrest.Matchers.is;

import org.hamcrest.Description;
import org.hamcrest.TypeSafeMatcher;

/**
 * A Hamcrest matcher for mathing a {@link PinyinSyllable}.
 */
public class PinyinSyllableMatcher extends TypeSafeMatcher<PinyinSyllable> {

	private final PinyinSyllable pinyinSyllable;

	public PinyinSyllableMatcher(final PinyinSyllable pinyinSyllable) {
		this.pinyinSyllable = pinyinSyllable;
	}

	@Override
	public void describeTo(final Description description) {
		final PinyinSyllable syllable = this.pinyinSyllable;
		this.describe(syllable, description);
	}

	private Description describe(final PinyinSyllable syllable, final Description description) {
		return description.appendText("<").appendText(syllable.getDisplayValue()).appendText(">");
	}

	@Override
	protected boolean matchesSafely(final PinyinSyllable item) {
		return is(this.pinyinSyllable.getLetters()).matches(item.getLetters())
				&& is(this.pinyinSyllable.getTone()).matches(item.getTone());
	}

	@Override
	protected void describeMismatchSafely(final PinyinSyllable item, final Description mismatchDescription) {
		mismatchDescription.appendText("was ");
		this.describe(item, mismatchDescription);
	}

}
