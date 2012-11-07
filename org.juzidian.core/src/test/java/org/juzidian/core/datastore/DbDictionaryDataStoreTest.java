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

	private DictionaryEntry createDictionaryEntry(final String chinese, final String english, final PinyinSyllable... pinyin) {
		return new BasicDictionaryEntry(chinese, chinese, Arrays.asList(pinyin), Arrays.asList(english.split(";")));
	}

	@Test
	public void findPinyinShouldFindMatchingEntriesWhenTonesNotProvided() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("nihao"));
		Assert.assertEquals(1, entries.size());
		Assert.assertEquals("你好", entries.get(0).getSimplified());
	}

	@Test
	public void findPinyinShouldFindMatchingEntriesWhenTonesProvided() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("ni3hao3"));
		Assert.assertEquals(1, entries.size());
		Assert.assertEquals("你好", entries.get(0).getSimplified());
	}

	@Test
	public void findPinyinShouldFindMatchingEntriesWhenSomeTonesProvided() {
		this.persistDefaultTestEntries();
		final List<DictionaryEntry> entries = this.dbDictionaryDataStore.findPinyin(this.pinyinParser.parse("ni3hao"));
		Assert.assertEquals(1, entries.size());
		Assert.assertEquals("你好", entries.get(0).getSimplified());
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
		Assert.assertEquals(1, entries.size());
		final DictionaryEntry entry = entries.get(0);
		Assert.assertEquals("好", entry.getSimplified());
	}

}
