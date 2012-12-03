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

import static org.hamcrest.Matchers.empty;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.hamcrest.CoreMatchers;
import org.hamcrest.FeatureMatcher;
import org.hamcrest.Matcher;
import org.hamcrest.collection.IsIterableContainingInOrder;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.core.PinyinParser;
import org.juzidian.core.PinyinSyllable;
import org.juzidian.core.Tone;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.jdbc.JdbcConnectionSource;

public class DbDictionaryDataStoreTest {

	private JdbcConnectionSource connectionSource;

	private Dao<DbDictionaryEntry, Long> dictionaryEntryDao;

	private DbDictionaryDataStore dbDictionaryDataStore;

	private PinyinParser pinyinParser;

	@Before
	public void setUp() throws Exception {
		this.connectionSource = new JdbcConnectionSource("jdbc:sqlite::memory:");
		new DbDictionaryDataStoreSchemaCreator().createSchema(this.connectionSource);
		this.dictionaryEntryDao = DaoManager.<Dao<DbDictionaryEntry, Long>, DbDictionaryEntry>createDao(this.connectionSource, DbDictionaryEntry.class);
		final Dao<DbDictionaryMetadata, Long> dictionaryMetadataDao = DaoManager.<Dao<DbDictionaryMetadata, Long>, DbDictionaryMetadata>createDao(this.connectionSource, DbDictionaryMetadata.class);
		this.dbDictionaryDataStore = new DbDictionaryDataStore(this.dictionaryEntryDao, dictionaryMetadataDao);
		this.dbDictionaryDataStore.createSchema();
		this.pinyinParser = new PinyinParser();
	}

	public void persistDefaultTestEntries() {
		this.persistEntry("好", "good; okay", new PinyinSyllable("hao", Tone.THIRD));
		this.persistEntry("好看", "attractive; good looking", new PinyinSyllable("hao", Tone.THIRD), new PinyinSyllable("kan", Tone.FOURTH));
		this.persistEntry("看", "to look", new PinyinSyllable("kan", Tone.FIRST));
		this.persistEntry("你好", "hello (greeting)", new PinyinSyllable("ni", Tone.THIRD), new PinyinSyllable("hao", Tone.THIRD));
	}

	private void persistEntry(final String chinese, final String english, final PinyinSyllable... pinyin) {
		this.dbDictionaryDataStore.add(this.createDictionaryEntry(chinese, english, pinyin));
	}

	private void persistEntry(final String chinese, final String pinyin, final String english) {
		this.persistEntry(chinese, english, new ArrayList<PinyinSyllable>(this.pinyinParser.parse(pinyin)).toArray(new PinyinSyllable[] {}));
	}

	private DictionaryEntry createDictionaryEntry(final String chinese, final String english, final PinyinSyllable... pinyin) {
		return new BasicDictionaryEntry(chinese, chinese, Arrays.asList(pinyin), Arrays.asList(english.split(";")));
	}

	private static Matcher<DictionaryEntry> entryWithSimplified(final String chineseWord) {
		return new FeatureMatcher<DictionaryEntry, String>(CoreMatchers.equalTo(chineseWord), "DictionaryEntry with simplified", "simplified") {
			@Override
			protected String featureValueOf(final DictionaryEntry actual) {
				return actual.getSimplified();
			}
		};
	}

	private static Matcher<Iterable<? extends DictionaryEntry>> containsSimplified(final String... simplifiedChineseWords) {
		final List<Matcher<? super DictionaryEntry>> matchers = new ArrayList<Matcher<? super DictionaryEntry>>();
		for (final String chineseWord : simplifiedChineseWords) {
			matchers.add(entryWithSimplified(chineseWord));
		}
		return new IsIterableContainingInOrder<DictionaryEntry>(matchers);
	}

	@Test
	public void findPinyinShouldFindMatchingEntriesWhenTonesNotProvided() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("nihao"), 25, 0);
		assertThat(entries, containsSimplified("你好"));
	}

	@Test
	public void findPinyinShouldFindMatchingEntriesWhenTonesProvided() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("ni3hao3"), 25, 0);
		assertThat(entries, containsSimplified("你好"));
	}

	@Test
	public void findPinyinShouldFindMatchingEntriesWhenSomeTonesProvided() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("ni3hao"), 25, 0);
		assertThat(entries, containsSimplified("你好"));
	}

	@Test
	public void findPinyinShouldFindNoMatchingEntriesWhenIncorrectTonesProvided() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("ni1hao3"), 25, 0);
		Assert.assertEquals(0, entries.size());
	}

	@Test
	public void findPinyinShouldReturnEntriesWithCorrectPinyin() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("hao3kan4"), 25, 0);
		Assert.assertEquals(1, entries.size());
		final DictionaryEntry entry = entries.get(0);
		Assert.assertEquals(2, entry.getPinyin().size());
		Assert.assertEquals(new PinyinSyllable("hao", Tone.THIRD), entry.getPinyin().get(0));
		Assert.assertEquals(new PinyinSyllable("kan", Tone.FOURTH), entry.getPinyin().get(1));
	}

	@Test
	public void findPinyinShouldOnlyReturnEntriesContainingSyllablesStartingWithGivenPinyin() {
		this.persistEntry("好", "good; okay", new PinyinSyllable("hao", Tone.THIRD));
		this.persistEntry("照相", "take photograph", new PinyinSyllable("zhao", Tone.FOURTH), new PinyinSyllable("xiang", Tone.FOURTH));
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("hao"), 25, 0);
		assertThat(entries, containsSimplified("好"));
	}

	@Test
	public void findPinyinShouldOrderExactSyllableMatchesBeforePartialMatches() {
		this.persistEntry("长", "to grow", new PinyinSyllable("zhang", Tone.THIRD));
		this.persistEntry("战", "war", new PinyinSyllable("zhan", Tone.FOURTH));
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("zhan"), 25, 0);
		assertThat(entries, containsSimplified("战", "长"));
	}

	@Test
	public void findPinyinShouldOrderLongerExactSyllableMatchesBeforeShorterPartialMatches() {
		this.persistEntry("长", "to grow", new PinyinSyllable("zhang", Tone.THIRD));
		this.persistEntry("战斗", "to battle", new PinyinSyllable("zhan", Tone.FOURTH));
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("zhan"), 25, 0);
		assertThat(entries, containsSimplified("战斗", "长"));
	}

	@Test
	public void findPinyinShouldOrderShorterWordsBeforeLongerWords() {
		this.persistEntry("狗肉", "dog meat", new PinyinSyllable("gou", Tone.THIRD), new PinyinSyllable("rou", Tone.FOURTH));
		this.persistEntry("够", "enough", new PinyinSyllable("gou", Tone.FOURTH));
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("gou"), 25, 0);
		assertThat(entries, containsSimplified("够", "狗肉"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findPinyinShouldRejectNegativeLimit() {
		this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("gou"), -1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findPinyinShouldRejectNegativeOffset() {
		this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("gou"), 25, -1);
	}

	@Test
	public void findPinyinShouldLimitEntriesToAmountSpecified() {
		this.persistEntry("好听", "hao3ting1", "good sounding");
		this.persistEntry("好看", "hao3kan4", "attractive; good looking");
		this.persistEntry("好棒", "hao3bang4", "excellent");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("hao"), 2, 0);
		assertThat(entries, containsSimplified("好棒", "好看"));
	}

	@Test
	public void findPinyinShouldSkipEntriesAfterIndexSpecified() {
		this.persistEntry("好听", "hao3ting1", "good sounding");
		this.persistEntry("好看", "hao3kan4", "attractive; good looking");
		this.persistEntry("好棒", "hao3bang4", "excellent");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("hao"), 2, 2);
		assertThat(entries, containsSimplified("好听"));
	}

	@Test
	public void findDefinitionsShouldReturnEmptyResultWhenNoEntriesMatchSearch() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("crabapples", 25, 0);
		assertThat(entries, is(empty()));
	}

	@Test
	public void findDefinitionsShouldOrderExactFirstMatchBeforeExactNonFirstMatch() {
		this.persistEntry("愉", "yu2", "joyful; happy");
		this.persistEntry("高兴", "gao1xing4", "happy; excited");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("happy", 25, 0);
		assertThat(entries, containsSimplified("高兴", "愉"));
	}

	@Test
	public void findDefinitionsShouldOrderExactMatchBeforeStartsWithMatch() {
		this.persistEntry("好看", "hao3kan4", "good looking");
		this.persistEntry("很好", "hen3hao3", "good");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("good", 25, 0);
		assertThat(entries, containsSimplified("很好", "好看"));
	}

	@Test
	public void findDefinitionsShouldOrderExactMatchBeforeStartsWithMatchWhenDefinitionIsNotFirst() {
		this.persistEntry("好看", "hao3kan4", "attractive; good looking");
		this.persistEntry("很好", "hen3hao3", "nice; good");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("good", 25, 0);
		assertThat(entries, containsSimplified("很好", "好看"));
	}

	@Test
	public void findDefinitionsShouldOrderStartsWithMatchBeforeContainsMatch() {
		this.persistEntry("好看", "hao3kan4", "good looking");
		this.persistEntry("看看", "kan4kan4", "look see");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("look", 25, 0);
		assertThat(entries, containsSimplified("看看", "好看"));
	}

	@Test
	public void findDefinitionsShouldOrderStartsWithMatchBeforeContainsMatchWhenDefinitionIsNotFirst() {
		this.persistEntry("好看", "hao3kan4", "attractive; good looking");
		this.persistEntry("看看", "kan4kan4", "have a look; look see");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("look", 25, 0);
		assertThat(entries, containsSimplified("看看", "好看"));
	}

	@Test
	public void findDefinitionsShouldOrderStartsWithMatchBeforeEndsWithMatch() {
		this.persistEntry("不好", "bu4hao3", "not good");
		this.persistEntry("好看", "hao3kan4", "good looking");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("good", 25, 0);
		assertThat(entries, containsSimplified("好看", "不好"));
	}

	@Test
	public void findDefinitionsShouldOrderEntriesByHanziLengthWhenEntriesAreBothExactMatches() {
		this.persistEntry("看看", "kan4kan4", "look");
		this.persistEntry("看", "kan4", "look");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("look", 25, 0);
		assertThat(entries, containsSimplified("看", "看看"));
	}

	@Test
	public void findDefinitionsShouldOrderEntriesByHanziLengthWhenEntriesAreBothStartsWithMatches() {
		this.persistEntry("你好", "ni3hao3", "hello (greeting)");
		this.persistEntry("喂", "wei4", "hello (on telephone)");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("hello", 25, 0);
		assertThat(entries, containsSimplified("喂", "你好"));
	}

	@Test
	public void findDefinitionsShouldOrderEntriesByHanziLengthWhenEntriesAreBothContainsMatches() {
		this.persistEntry("很好看", "hao3kan4", "very good looking");
		this.persistEntry("好听", "hao3ting1", "sound good");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("good", 25, 0);
		assertThat(entries, containsSimplified("好听", "很好看"));
	}

	@Test
	public void findDefinitionsShouldOrderEntriesByPinyinWhenEntriesHaveSameLength() {
		this.persistEntry("好听", "hao3ting1", "sound good");
		this.persistEntry("好看", "hao3kan4", "look good");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("good", 25, 0);
		assertThat(entries, containsSimplified("好看", "好听"));
	}

	@Test
	public void findDefinitionsShouldOrderWholeWordPartialMatchBeforePartialWordPartialMatch() {
		this.persistEntry("不能", "bu4neng2", "cannot");
		this.persistEntry("可作", "ke3zuo4", "can be used for");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("can", 25, 0);
		assertThat(entries, containsSimplified("可作", "不能"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findDefinitionsShouldRejectNegativeLimit() {
		this.dbDictionaryDataStore.findDefinitions("good", -1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findDefinitionsShouldRejectNegativeOffset() {
		this.dbDictionaryDataStore.findDefinitions("good", 25, -1);
	}

	@Test
	public void findDefinitionsShouldLimitEntriesToAmountSpecified() {
		this.persistEntry("不好", "bu4hao3", "not good");
		this.persistEntry("好看", "hao3kan4", "attractive; good looking");
		this.persistEntry("好听", "hao3ting1", "good sounding");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("good", 2, 0);
		assertThat(entries, containsSimplified("好看", "好听"));
	}

	@Test
	public void findDefinitionsShouldSkipEntriesAfterIndexSpecified() {
		this.persistEntry("不好", "bu4hao3", "not good");
		this.persistEntry("好看", "hao3kan4", "attractive; good looking");
		this.persistEntry("好听", "hao3ting1", "good sounding");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("good", 2, 2);
		assertThat(entries, containsSimplified("不好"));
	}

	@Test
	public void findHanziShouldOrderStartsWithMatchBeforeContainsMatch() {
		this.persistEntry("你好", "ni3hao3", "hello (greeting)");
		this.persistEntry("好看", "hao3kan4", "attractive; good looking");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findChinese("好", 25, 0);
		assertThat(entries, containsSimplified("好看", "你好"));
	}

	@Test
	public void findHanziShouldOrderEntriesByPinyinWhenEntriesAreBothExactMatches() {
		this.persistEntry("长", "zhang3", "to grow");
		this.persistEntry("长", "chang2", "long");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findChinese("长", 25, 0);
		Assert.assertEquals("Result count", 2, entries.size());
		Assert.assertEquals("First result entry", "chang", entries.get(0).getPinyin().get(0).getLetters());
		Assert.assertEquals("Second result entry", "zhang", entries.get(1).getPinyin().get(0).getLetters());
	}

	@Test
	public void findHanziShouldOrderEntriesByHanziLengthWhenEntriesAreBothStartsWithMatches() {
		this.persistEntry("好久不见", "hao3jiu3bu4jian4", "long time no see");
		this.persistEntry("好看", "hao3kan4", "good looking");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findChinese("好", 25, 0);
		assertThat(entries, containsSimplified("好看", "好久不见"));
	}

	@Test
	public void findHanziShouldOrderEntriesByHanziLengthWhenEntriesAreBothContainsMatches() {
		this.persistEntry("好久不见", "hao3jiu3bu4jian4", "long time no see");
		this.persistEntry("看见", "kan4jian4", "to see");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findChinese("见", 25, 0);
		assertThat(entries, containsSimplified("看见", "好久不见"));
	}

	@Test
	public void findHanziShouldOrderEntriesByPinyinWhenEntriesAreBothStartsWithMatchesAndSameLength() {
		this.persistEntry("好听", "hao3ting1", "good sounding");
		this.persistEntry("好看", "hao3kan4", "good looking");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findChinese("好", 25, 0);
		assertThat(entries, containsSimplified("好看", "好听"));
	}

	@Test
	public void findHanziShouldOrderEntriesByPinyinWhenEntriesAreBothContainsMatchesAndSameLength() {
		this.persistEntry("你好", "ni3hao3", "hello (greeting)");
		this.persistEntry("不好", "bu4hao3", "not good");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findChinese("好", 25, 0);
		assertThat(entries, containsSimplified("不好", "你好"));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findHanziShouldRejectNegativeLimit() {
		this.dbDictionaryDataStore.findChinese("好", -1, 0);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findHanziShouldRejectNegativeOffset() {
		this.dbDictionaryDataStore.findChinese("好", 25, -1);
	}

	@Test
	public void findHanziShouldLimitEntriesToAmountSpecified() {
		this.persistEntry("你好", "ni3hao3", "hello (greeting)");
		this.persistEntry("好看", "hao3kan4", "attractive; good looking");
		this.persistEntry("好棒", "hao3bang4", "excellent");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findChinese("好", 2, 0);
		assertThat(entries, containsSimplified("好棒", "好看"));
	}

	@Test
	public void findHanziShouldSkipEntriesAfterIndexSpecified() {
		this.persistEntry("你好", "ni3hao3", "hello (greeting)");
		this.persistEntry("好看", "hao3kan4", "attractive; good looking");
		this.persistEntry("好棒", "hao3bang4", "excellent");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findChinese("好", 2, 2);
		assertThat(entries, containsSimplified("你好"));
	}

}
