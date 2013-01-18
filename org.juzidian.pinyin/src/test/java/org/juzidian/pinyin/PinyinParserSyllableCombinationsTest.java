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

import java.util.List;

import org.junit.Ignore;
import org.junit.Test;
import org.juzidian.pinyin.PinyinHelper;
import org.juzidian.pinyin.PinyinSyllable;

public class PinyinParserSyllableCombinationsTest {

	@Ignore
	@Test
	public void parseAllThreeSyllableCombinations() {
		final PinyinHelper pinyinHelper = new PinyinHelper();
		final int syllableCount = pinyinHelper.getValidSyllables().size();
		final int combinations = syllableCount * syllableCount * syllableCount;
		int count = 0;
		for (final String syllable1 : pinyinHelper.getValidSyllables()) {
			for (final String syllable2 : pinyinHelper.getValidSyllables()) {
				for (final String syllable3 : pinyinHelper.getValidSyllables()) {
					count += 1;
					final String pinyin = syllable1 + syllable2 + syllable3;
					try {
						final List<PinyinSyllable> syllables = new PinyinParser().parse(pinyin);
						if (count % 0xffff == 0) {
							final double progress = ((double) count) / combinations;
							System.out.print(String.format("%.2f%% : %s :", progress * 100, pinyin));
							System.out.println(
									syllables.get(0).getLetters() +
									(syllables.size() > 1 ? " " + syllables.get(1).getLetters() : "") +
									(syllables.size() > 2 ? " " + syllables.get(2).getLetters() : "")
									);
						}
					} catch (final Exception e) {
						System.out.print("failed to parse pinyin: " + pinyin);
						e.printStackTrace();
					}
				}
			}
		}
	}

	@Ignore
	@Test
	public void parseAllTwoSyllableCombinations() {
		final PinyinHelper pinyinHelper = new PinyinHelper();
		final int syllableCount = pinyinHelper.getValidSyllables().size();
		final int combinations = syllableCount * syllableCount;
		int count = 0;
		for (final String syllable1 : pinyinHelper.getValidSyllables()) {
			for (final String syllable2 : pinyinHelper.getValidSyllables()) {
				count += 1;
				final String pinyin = syllable1 + syllable2;
				try {
					final List<PinyinSyllable> syllables = new PinyinParser().parse(pinyin);
					final double progress = ((double) count) / combinations;
					System.out.print(String.format("%.3f%% : %s :", progress * 100, pinyin));
					System.out.println(
							syllables.get(0).getLetters() +
							(syllables.size() > 1 ? " " + syllables.get(1).getLetters() : "")
							);
				} catch (final Exception e) {
					System.out.print("failed to parse pinyin: " + pinyin);
					e.printStackTrace();
				}
			}
		}
	}

}
