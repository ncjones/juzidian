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

import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.juzidian.pinyin.PinyinSyllable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.j256.ormlite.dao.Dao;

public class DbDictionaryDataStoreTest {

	@Mock
	private Dao<DbDictionaryEntry, Long> dictionaryEntryDao;

	@Mock
	private Dao<DbDictionaryMetadata, Long> dictionaryMetadataDao;

	private DbDictionaryDataStore dbDictionaryDataStore;

	@Before
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.dbDictionaryDataStore = new DbDictionaryDataStore(this.dictionaryEntryDao, dictionaryMetadataDao);
	}

	private static List<PinyinSyllable> pinyinSyllables(final String syllable) {
		return Arrays.asList(new PinyinSyllable(syllable));
	}

	@Test(expected = IllegalArgumentException.class)
	public void findPinyinShouldRejectNegativeLimit() {
		this.dbDictionaryDataStore.findPinyin(pinyinSyllables("gou"), -1, 0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findPinyinShouldRejectNegativeOffset() {
		this.dbDictionaryDataStore.findPinyin(pinyinSyllables("gou"), 25, -1, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findDefinitionsShouldRejectNegativeLimit() {
		this.dbDictionaryDataStore.findDefinitions("good", -1, 0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findDefinitionsShouldRejectNegativeOffset() {
		this.dbDictionaryDataStore.findDefinitions("good", 25, -1, null);
	}


	@Test(expected = IllegalArgumentException.class)
	public void findHanziShouldRejectNegativeLimit() {
		this.dbDictionaryDataStore.findChinese("好", -1, 0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findHanziShouldRejectNegativeOffset() {
		this.dbDictionaryDataStore.findChinese("好", 25, -1, null);
	}

	@Test
	public void populateMetadataShouldSaveCurrentFormatVersionToMetadataTable() throws Exception {
		ArgumentCaptor<DbDictionaryMetadata> captor = ArgumentCaptor.forClass(DbDictionaryMetadata.class);
		this.dbDictionaryDataStore.populateMetadata();
		verify(dictionaryMetadataDao).createOrUpdate(captor.capture());
		DbDictionaryMetadata metadata = captor.getValue();
		assertThat(metadata.getId(), is(1L));
		assertThat(metadata.getVersion(), is(DbDictionaryDataStore.DATA_FORMAT_VERSION));
	}

	@Test
	public void currentFormatVersionShouldRetrieveVersionFromMetadataTable() throws Exception {
		final DbDictionaryMetadata metadata = Mockito.mock(DbDictionaryMetadata.class);
		when(metadata.getVersion()).thenReturn(5);
		when(dictionaryMetadataDao.queryForId(1L)).thenReturn(metadata);
		assertThat(this.dbDictionaryDataStore.getCurrentDataFormatVersion(), equalTo(5));
	}

}
