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
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.anyLong;
import static org.mockito.Matchers.anyString;
import static org.mockito.Matchers.anyVararg;
import static org.mockito.Matchers.isA;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
import org.juzidian.core.SearchCanceller;
import org.juzidian.core.SearchCanceller.Listener;
import org.juzidian.pinyin.PinyinSyllable;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.MockitoAnnotations;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.ArgumentHolder;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;
import com.j256.ormlite.stmt.Where;
import com.j256.ormlite.support.CancellationSignaller;
import com.j256.ormlite.support.CancellationSignaller.Cancellable;

public class DbDictionaryDataStoreTest {

	@Mock
	private Dao<DbDictionaryEntry, Long> dictionaryEntryDao;

	@Mock
	private Dao<DbDictionaryMetadata, Long> dictionaryMetadataDao;

	private DbDictionaryDataStore dbDictionaryDataStore;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.dbDictionaryDataStore = new DbDictionaryDataStore(this.dictionaryEntryDao, dictionaryMetadataDao);
		QueryBuilder<DbDictionaryEntry, Long> mockQueryBuilder = Mockito.mock(QueryBuilder.class);
		Where<DbDictionaryEntry, Long> mockWhere = Mockito.mock(Where.class);
		when(mockQueryBuilder.limit(anyLong())).thenReturn(mockQueryBuilder);
		when(mockQueryBuilder.offset(anyLong())).thenReturn(mockQueryBuilder);
		when(mockQueryBuilder.orderByRaw(anyString(), (ArgumentHolder[]) anyVararg())).thenReturn(mockQueryBuilder);
		when(mockQueryBuilder.where()).thenReturn(mockWhere);
		when(mockWhere.like(anyString(), any())).thenReturn(mockWhere);
		when(this.dictionaryEntryDao.queryBuilder()).thenReturn(mockQueryBuilder);
	}

	private static List<PinyinSyllable> pinyinSyllables(final String syllable) {
		return Arrays.asList(new PinyinSyllable(syllable));
	}

	@SuppressWarnings("unchecked")
	private static PreparedQuery<DbDictionaryEntry> anyDictionaryEntryPreparedQuery() {
		return any(PreparedQuery.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findPinyinShouldRejectNegativeLimit() {
		this.dbDictionaryDataStore.findPinyin(pinyinSyllables("gou"), -1, 0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findPinyinShouldRejectNegativeOffset() {
		this.dbDictionaryDataStore.findPinyin(pinyinSyllables("gou"), 25, -1, null);
	}

	@Test
	public void findPinyinShouldRegisterCancellationListener() {
		SearchCanceller canceller = Mockito.mock(SearchCanceller.class);
		dbDictionaryDataStore.findPinyin(pinyinSyllables("wei"), 25, 0, canceller);
		verify(canceller).register(isA(Listener.class));
	}

	@Test
	public void findPinyinShouldBindCancellerToOrmliteQueryCancellationSignal() throws Exception {
		SearchCanceller canceller = new SearchCanceller();
		Cancellable mockOrmliteCancellable = Mockito.mock(Cancellable.class);
		ArgumentCaptor<CancellationSignaller> ormliteSignallerCaptor = ArgumentCaptor.forClass(CancellationSignaller.class);
		dbDictionaryDataStore.findPinyin(pinyinSyllables("wei"), 25, 0, canceller);
		verify(this.dictionaryEntryDao).query(anyDictionaryEntryPreparedQuery(), ormliteSignallerCaptor.capture());
		CancellationSignaller ormliteSignaller = ormliteSignallerCaptor.getValue();
		ormliteSignaller.attach(mockOrmliteCancellable);
		canceller.cancel();
		verify(mockOrmliteCancellable).cancel();
	}

	@Test
	public void findPinyinShouldQueryWithoutCancellationSignalWhenCancellerIsNull() throws Exception {
		dbDictionaryDataStore.findPinyin(pinyinSyllables("wei"), 25, 0, null);
		verify(this.dictionaryEntryDao).query(anyDictionaryEntryPreparedQuery());
	}

	@Test(expected = IllegalArgumentException.class)
	public void findDefinitionsShouldRejectNegativeLimit() {
		this.dbDictionaryDataStore.findDefinitions("good", -1, 0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findDefinitionsShouldRejectNegativeOffset() {
		this.dbDictionaryDataStore.findDefinitions("good", 25, -1, null);
	}

	@Test
	public void findDefinitionsShouldRegisterCancellationListener() {
		SearchCanceller canceller = Mockito.mock(SearchCanceller.class);
		dbDictionaryDataStore.findDefinitions("hello", 25, 0, canceller);
		verify(canceller).register(isA(Listener.class));
	}

	@Test
	public void findDefinitionsShouldBindCancellerToOrmliteQueryCancellationSignal() throws Exception {
		SearchCanceller canceller = new SearchCanceller();
		Cancellable mockOrmliteCancellable = Mockito.mock(Cancellable.class);
		ArgumentCaptor<CancellationSignaller> ormliteSignallerCaptor = ArgumentCaptor.forClass(CancellationSignaller.class);
		dbDictionaryDataStore.findDefinitions("hello", 25, 0, canceller);
		verify(this.dictionaryEntryDao).query(anyDictionaryEntryPreparedQuery(), ormliteSignallerCaptor.capture());
		CancellationSignaller ormliteSignaller = ormliteSignallerCaptor.getValue();
		ormliteSignaller.attach(mockOrmliteCancellable);
		canceller.cancel();
		verify(mockOrmliteCancellable).cancel();
	}

	@Test
	public void findDefinitionsShouldQueryWithoutCancellationSignalWhenCancellerIsNull() throws Exception {
		dbDictionaryDataStore.findDefinitions("hello", 25, 0, null);
		verify(this.dictionaryEntryDao).query(anyDictionaryEntryPreparedQuery());
	}

	@Test(expected = IllegalArgumentException.class)
	public void findChineseShouldRejectNegativeLimit() {
		this.dbDictionaryDataStore.findChinese("好", -1, 0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findChineseShouldRejectNegativeOffset() {
		this.dbDictionaryDataStore.findChinese("好", 25, -1, null);
	}

	@Test
	public void findChineseShouldRegisterCancellationListener() {
		SearchCanceller canceller = Mockito.mock(SearchCanceller.class);
		dbDictionaryDataStore.findChinese("好", 25, 0, canceller);
		verify(canceller).register(isA(Listener.class));
	}

	@Test
	public void findChineseShouldBindCancellerToOrmliteQueryCancellationSignal() throws Exception {
		SearchCanceller canceller = new SearchCanceller();
		Cancellable mockOrmliteCancellable = Mockito.mock(Cancellable.class);
		ArgumentCaptor<CancellationSignaller> ormliteSignallerCaptor = ArgumentCaptor.forClass(CancellationSignaller.class);
		dbDictionaryDataStore.findChinese("好", 25, 0, canceller);
		verify(this.dictionaryEntryDao).query(anyDictionaryEntryPreparedQuery(), ormliteSignallerCaptor.capture());
		CancellationSignaller ormliteSignaller = ormliteSignallerCaptor.getValue();
		ormliteSignaller.attach(mockOrmliteCancellable);
		canceller.cancel();
		verify(mockOrmliteCancellable).cancel();
	}

	@Test
	public void findChineseShouldQueryWithoutCancellationSignalWhenCancellerIsNull() throws Exception {
		dbDictionaryDataStore.findDefinitions("好", 25, 0, null);
		verify(this.dictionaryEntryDao).query(anyDictionaryEntryPreparedQuery());
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
