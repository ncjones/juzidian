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
package org.juzidian.pinyin;

import static org.hamcrest.Matchers.equalTo;
import static org.junit.Assert.assertThat;
import static org.juzidian.pinyin.Tone.ANY;
import static org.juzidian.pinyin.Tone.FIRST;
import static org.juzidian.pinyin.Tone.FOURTH;
import static org.juzidian.pinyin.Tone.NEUTRAL;
import static org.juzidian.pinyin.Tone.SECOND;
import static org.juzidian.pinyin.Tone.THIRD;

import java.util.Arrays;
import java.util.Collection;

import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;
import org.junit.runners.Parameterized.Parameters;
import org.juzidian.pinyin.Tone;

@RunWith(Parameterized.class)
public class ToneDiacriticCharacterTest {

	private final Tone tone;

	private final char vowel;

	private final char expectedDiacriticCharacter;

	public ToneDiacriticCharacterTest(final Tone tone, final char vowel, final char expectedDiacriticCharacter) {
		this.tone = tone;
		this.vowel = vowel;
		this.expectedDiacriticCharacter = expectedDiacriticCharacter;
	}

	@Parameters(name = "Given ''{1}'', tone {0} should produce diacritic ''{2}''")
	public static Collection<Object[]> data() {
		return Arrays.asList(new Object[][] {
				{ FIRST, 'a', 'ā' },
				{ FIRST, 'e', 'ē' },
				{ FIRST, 'i', 'ī' },
				{ FIRST, 'o', 'ō' },
				{ FIRST, 'u', 'ū' },
				{ FIRST, 'ü', 'ǖ' },
				{ SECOND, 'a', 'á' },
				{ SECOND, 'e', 'é' },
				{ SECOND, 'i', 'í' },
				{ SECOND, 'o', 'ó' },
				{ SECOND, 'u', 'ú' },
				{ SECOND, 'ü', 'ǘ' },
				{ THIRD, 'a', 'ǎ' },
				{ THIRD, 'e', 'ĕ' },
				{ THIRD, 'i', 'ǐ' },
				{ THIRD, 'o', 'ǒ' },
				{ THIRD, 'u', 'ǔ' },
				{ THIRD, 'ü', 'ǚ' },
				{ FOURTH, 'a', 'à' },
				{ FOURTH, 'e', 'è' },
				{ FOURTH, 'i', 'ì' },
				{ FOURTH, 'o', 'ò' },
				{ FOURTH, 'u', 'ù' },
				{ FOURTH, 'ü', 'ǜ' },
				{ NEUTRAL, 'a', 'a' },
				{ NEUTRAL, 'e', 'e' },
				{ NEUTRAL, 'i', 'i' },
				{ NEUTRAL, 'o', 'o' },
				{ NEUTRAL, 'u', 'u' },
				{ NEUTRAL, 'ü', 'ü' },
				{ ANY, 'a', 'a' },
				{ ANY, 'e', 'e' },
				{ ANY, 'i', 'i' },
				{ ANY, 'o', 'o' },
				{ ANY, 'u', 'u' },
				{ ANY, 'ü', 'ü' },
		});
	}

	@Test
	public void test() {
		assertThat(this.tone.getDiacriticCharacter(this.vowel), equalTo(this.expectedDiacriticCharacter));
	}

}
