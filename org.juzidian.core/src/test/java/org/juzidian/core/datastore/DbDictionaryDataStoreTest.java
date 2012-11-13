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

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import junit.framework.Assert;

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

	private void verifySearchResults(final List<DictionaryEntry> entries, final String... expectedChineseWords) {
		Assert.assertEquals("Result count", expectedChineseWords.length, entries.size());
		for (int i = 0; i < expectedChineseWords.length; i++) {
			final DictionaryEntry entry = entries.get(i);
			Assert.assertEquals("Result entry at index " + i, expectedChineseWords[i], entry.getSimplified());
		}
	}

	@Test
	public void findPinyinShouldFindMatchingEntriesWhenTonesNotProvided() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("nihao"));
		this.verifySearchResults(entries, "你好");
	}

	@Test
	public void findPinyinShouldFindMatchingEntriesWhenTonesProvided() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("ni3hao3"));
		this.verifySearchResults(entries, "你好");
	}

	@Test
	public void findPinyinShouldFindMatchingEntriesWhenSomeTonesProvided() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("ni3hao"));
		this.verifySearchResults(entries, "你好");
	}

	@Test
	public void findPinyinShouldFindNoMatchingEntriesWhenIncorrectTonesProvided() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("ni1hao3"));
		Assert.assertEquals(0, entries.size());
	}

	@Test
	public void findPinyinShouldReturnEntriesWithCorrectPinyin() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("hao3kan4"));
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
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("hao"));
		this.verifySearchResults(entries, "好");
	}

	@Test
	public void findPinyinShouldOrderExactSyllableMatchesBeforePartialMatches() {
		this.persistEntry("长", "to grow", new PinyinSyllable("zhang", Tone.THIRD));
		this.persistEntry("战", "war", new PinyinSyllable("zhan", Tone.FOURTH));
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("zhan"));
		this.verifySearchResults(entries, "战", "长");
	}

	@Test
	public void findPinyinShouldOrderLongerExactSyllableMatchesBeforeShorterPartialMatches() {
		this.persistEntry("长", "to grow", new PinyinSyllable("zhang", Tone.THIRD));
		this.persistEntry("战斗", "to battle", new PinyinSyllable("zhan", Tone.FOURTH));
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("zhan"));
		this.verifySearchResults(entries, "战斗", "长");
	}

	@Test
	public void findPinyinShouldOrderShorterWordsBeforeLongerWords() {
		this.persistEntry("狗肉", "dog meat", new PinyinSyllable("gou", Tone.THIRD), new PinyinSyllable("rou", Tone.FOURTH));
		this.persistEntry("够", "enough", new PinyinSyllable("gou", Tone.FOURTH));
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("gou"));
		this.verifySearchResults(entries, "够", "狗肉");
	}

	@Test
	public void findDefinitionsShouldReturnEmptyResultWhenNoEntriesMatchSearch() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("crabapples");
		this.verifySearchResults(entries);
	}

	@Test
	public void findDefinitionsShouldOrderExactFirstMatchBeforeExactNonFirstMatch() {
		this.persistEntry("愉", "yu2", "joyful; happy");
		this.persistEntry("高兴", "gao1xing4", "happy; excited");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("happy");
		this.verifySearchResults(entries, "高兴", "愉");
	}

	@Test
	public void findDefinitionsShouldOrderExactMatchBeforeStartsWithMatch() {
		this.persistEntry("好看", "hao3kan4", "good looking");
		this.persistEntry("很好", "hen3hao3", "good");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("good");
		this.verifySearchResults(entries, "很好", "好看");
	}

	@Test
	public void findDefinitionsShouldOrderExactMatchBeforeStartsWithMatchWhenDefinitionIsNotFirst() {
		this.persistEntry("好看", "hao3kan4", "attractive; good looking");
		this.persistEntry("很好", "hen3hao3", "nice; good");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("good");
		this.verifySearchResults(entries, "很好", "好看");
	}

	@Test
	public void findDefinitionsShouldOrderStartsWithMatchBeforeContainsMatch() {
		this.persistEntry("好看", "hao3kan4", "good looking");
		this.persistEntry("看看", "kan4kan4", "look see");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("look");
		this.verifySearchResults(entries, "看看", "好看");
	}

	@Test
	public void findDefinitionsShouldOrderStartsWithMatchBeforeContainsMatchWhenDefinitionIsNotFirst() {
		this.persistEntry("好看", "hao3kan4", "attractive; good looking");
		this.persistEntry("看看", "kan4kan4", "have a look; look see");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("look");
		this.verifySearchResults(entries, "看看", "好看");
	}

	@Test
	public void findDefinitionsShouldOrderEntriesByHanziLengthWhenEntriesAreBothExactMatches() {
		this.persistEntry("看看", "kan4kan4", "look");
		this.persistEntry("看", "kan4", "look");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("look");
		this.verifySearchResults(entries, "看", "看看");
	}

	@Test
	public void findDefinitionsShouldOrderEntriesByHanziLengthWhenEntriesAreBothStartsWithMatches() {
		this.persistEntry("你好", "ni3hao3", "hello (greeting)");
		this.persistEntry("喂", "wei4", "hello (on telephone)");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("hello");
		this.verifySearchResults(entries, "喂", "你好");
	}

	@Test
	public void findDefinitionsShouldOrderEntriesByHanziLengthWhenEntriesAreBothContainsMatches() {
		this.persistEntry("很好看", "hao3kan4", "very good looking");
		this.persistEntry("好听", "hao3ting1", "sound good");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("good");
		this.verifySearchResults(entries, "好听", "很好看");
	}

	@Test
	public void findDefinitionsShouldOrderEntriesByPinyinWhenEntriesHaveSameLength() {
		this.persistEntry("好听", "hao3ting1", "sound good");
		this.persistEntry("好看", "hao3kan4", "look good");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("good");
		this.verifySearchResults(entries, "好看", "好听");
	}

	@Test
	public void findDefinitionsShouldOrderWholeWordPartialMatchBeforePartialWordPartialMatch() {
		this.persistEntry("不能", "bu4neng2", "cannot");
		this.persistEntry("可作", "ke3zuo4", "can be used for");
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findDefinitions("can");
		this.verifySearchResults(entries, "可作", "不能");
	}
}
