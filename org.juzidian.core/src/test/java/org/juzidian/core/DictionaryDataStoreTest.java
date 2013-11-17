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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

import org.junit.Before;
import org.junit.Test;
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

public class DictionaryDataStoreTest {

	@Mock
	private Dao<DictionaryDataStoreEntry, Long> dictionaryEntryDao;

	@Mock
	private Dao<DictionaryDataStoreMetadata, Long> dictionaryMetadataDao;

	private DictionaryDataStore dictionaryDataStore;

	@Before
	@SuppressWarnings("unchecked")
	public void setUp() throws Exception {
		MockitoAnnotations.initMocks(this);
		this.dictionaryDataStore = new DictionaryDataStore(this.dictionaryEntryDao, dictionaryMetadataDao);
		QueryBuilder<DictionaryDataStoreEntry, Long> mockQueryBuilder = Mockito.mock(QueryBuilder.class);
		Where<DictionaryDataStoreEntry, Long> mockWhere = Mockito.mock(Where.class);
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
	private static PreparedQuery<DictionaryDataStoreEntry> anyDictionaryEntryPreparedQuery() {
		return any(PreparedQuery.class);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findPinyinShouldRejectNegativeLimit() {
		this.dictionaryDataStore.findPinyin(pinyinSyllables("gou"), -1, 0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findPinyinShouldRejectNegativeOffset() {
		this.dictionaryDataStore.findPinyin(pinyinSyllables("gou"), 25, -1, null);
	}

	@Test
	public void findPinyinShouldRegisterCancellationListener() {
		SearchCanceller canceller = Mockito.mock(SearchCanceller.class);
		dictionaryDataStore.findPinyin(pinyinSyllables("wei"), 25, 0, canceller);
		verify(canceller).register(isA(Listener.class));
	}

	@Test
	public void findPinyinShouldBindCancellerToOrmliteQueryCancellationSignal() throws Exception {
		SearchCanceller canceller = new SearchCanceller();
		Cancellable mockOrmliteCancellable = Mockito.mock(Cancellable.class);
		ArgumentCaptor<CancellationSignaller> ormliteSignallerCaptor = ArgumentCaptor.forClass(CancellationSignaller.class);
		dictionaryDataStore.findPinyin(pinyinSyllables("wei"), 25, 0, canceller);
		verify(this.dictionaryEntryDao).query(anyDictionaryEntryPreparedQuery(), ormliteSignallerCaptor.capture());
		CancellationSignaller ormliteSignaller = ormliteSignallerCaptor.getValue();
		ormliteSignaller.attach(mockOrmliteCancellable);
		canceller.cancel();
		verify(mockOrmliteCancellable).cancel();
	}

	@Test
	public void findPinyinShouldQueryWithoutCancellationSignalWhenCancellerIsNull() throws Exception {
		dictionaryDataStore.findPinyin(pinyinSyllables("wei"), 25, 0, null);
		verify(this.dictionaryEntryDao).query(anyDictionaryEntryPreparedQuery());
	}

	@Test(expected = DictionaryDataStoreException.class)
	public void findPinyinShouldThrowExceptionWhenQueryCreationFails() throws Exception {
		when(dictionaryEntryDao.queryBuilder().offset(anyLong())).thenThrow(new SQLException());
		dictionaryDataStore.findPinyin(pinyinSyllables("bang"), 25, 0, new SearchCanceller());
	}

	@Test(expected = DictionaryDataStoreException.class)
	public void findPinyinShouldThrowExceptionWhenQueryFails() throws Exception {
		when(dictionaryEntryDao.query(anyDictionaryEntryPreparedQuery(), isA(CancellationSignaller.class))).thenThrow(new SQLException());
		dictionaryDataStore.findPinyin(pinyinSyllables("bang"), 25, 0, new SearchCanceller());
	}

	@Test(expected = DictionaryDataStoreQueryCancelledException.class)
	public void findPinyinShouldThrowExceptionWhenQueryCancelled() throws Exception {
		when(dictionaryEntryDao.query(anyDictionaryEntryPreparedQuery(), isA(CancellationSignaller.class))).thenThrow(
				new SQLException("ORMLITE: query cancelled"));
		dictionaryDataStore.findPinyin(pinyinSyllables("bang"), 25, 0, new SearchCanceller());
	}

	@Test(expected = IllegalArgumentException.class)
	public void findDefinitionsShouldRejectNegativeLimit() {
		this.dictionaryDataStore.findDefinitions("good", -1, 0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findDefinitionsShouldRejectNegativeOffset() {
		this.dictionaryDataStore.findDefinitions("good", 25, -1, null);
	}

	@Test
	public void findDefinitionsShouldRegisterCancellationListener() {
		SearchCanceller canceller = Mockito.mock(SearchCanceller.class);
		dictionaryDataStore.findDefinitions("hello", 25, 0, canceller);
		verify(canceller).register(isA(Listener.class));
	}

	@Test
	public void findDefinitionsShouldBindCancellerToOrmliteQueryCancellationSignal() throws Exception {
		SearchCanceller canceller = new SearchCanceller();
		Cancellable mockOrmliteCancellable = Mockito.mock(Cancellable.class);
		ArgumentCaptor<CancellationSignaller> ormliteSignallerCaptor = ArgumentCaptor.forClass(CancellationSignaller.class);
		dictionaryDataStore.findDefinitions("hello", 25, 0, canceller);
		verify(this.dictionaryEntryDao).query(anyDictionaryEntryPreparedQuery(), ormliteSignallerCaptor.capture());
		CancellationSignaller ormliteSignaller = ormliteSignallerCaptor.getValue();
		ormliteSignaller.attach(mockOrmliteCancellable);
		canceller.cancel();
		verify(mockOrmliteCancellable).cancel();
	}

	@Test
	public void findDefinitionsShouldQueryWithoutCancellationSignalWhenCancellerIsNull() throws Exception {
		dictionaryDataStore.findDefinitions("hello", 25, 0, null);
		verify(this.dictionaryEntryDao).query(anyDictionaryEntryPreparedQuery());
	}

	@Test(expected = DictionaryDataStoreException.class)
	public void findDefinitionsShouldThrowExceptionWhenQueryCreationFails() throws Exception {
		when(dictionaryEntryDao.queryBuilder().offset(anyLong())).thenThrow(new SQLException());
		dictionaryDataStore.findDefinitions("hello", 25, 0, new SearchCanceller());
	}

	@Test(expected = DictionaryDataStoreException.class)
	public void findDefinitionsShouldThrowExceptionWhenQueryFails() throws Exception {
		when(dictionaryEntryDao.query(anyDictionaryEntryPreparedQuery(), isA(CancellationSignaller.class))).thenThrow(new SQLException());
		dictionaryDataStore.findDefinitions("hello", 25, 0, new SearchCanceller());
	}

	@Test(expected = DictionaryDataStoreQueryCancelledException.class)
	public void findDefinitionsShouldThrowExceptionWhenQueryCancelled() throws Exception {
		when(dictionaryEntryDao.query(anyDictionaryEntryPreparedQuery(), isA(CancellationSignaller.class))).thenThrow(
				new SQLException("ORMLITE: query cancelled"));
		dictionaryDataStore.findDefinitions("hello", 25, 0, new SearchCanceller());
	}

	@Test(expected = IllegalArgumentException.class)
	public void findChineseShouldRejectNegativeLimit() {
		this.dictionaryDataStore.findChinese("好", -1, 0, null);
	}

	@Test(expected = IllegalArgumentException.class)
	public void findChineseShouldRejectNegativeOffset() {
		this.dictionaryDataStore.findChinese("好", 25, -1, null);
	}

	@Test
	public void findChineseShouldRegisterCancellationListener() {
		SearchCanceller canceller = Mockito.mock(SearchCanceller.class);
		dictionaryDataStore.findChinese("好", 25, 0, canceller);
		verify(canceller).register(isA(Listener.class));
	}

	@Test
	public void findChineseShouldBindCancellerToOrmliteQueryCancellationSignal() throws Exception {
		SearchCanceller canceller = new SearchCanceller();
		Cancellable mockOrmliteCancellable = Mockito.mock(Cancellable.class);
		ArgumentCaptor<CancellationSignaller> ormliteSignallerCaptor = ArgumentCaptor.forClass(CancellationSignaller.class);
		dictionaryDataStore.findChinese("好", 25, 0, canceller);
		verify(this.dictionaryEntryDao).query(anyDictionaryEntryPreparedQuery(), ormliteSignallerCaptor.capture());
		CancellationSignaller ormliteSignaller = ormliteSignallerCaptor.getValue();
		ormliteSignaller.attach(mockOrmliteCancellable);
		canceller.cancel();
		verify(mockOrmliteCancellable).cancel();
	}

	@Test
	public void findChineseShouldQueryWithoutCancellationSignalWhenCancellerIsNull() throws Exception {
		dictionaryDataStore.findChinese("好", 25, 0, null);
		verify(this.dictionaryEntryDao).query(anyDictionaryEntryPreparedQuery());
	}

	@Test(expected = DictionaryDataStoreException.class)
	public void findChineseShouldThrowExceptionWhenQueryCreationFails() throws Exception {
		when(dictionaryEntryDao.queryBuilder().offset(anyLong())).thenThrow(new SQLException());
		dictionaryDataStore.findChinese("好", 25, 0, new SearchCanceller());
	}

	@Test(expected = DictionaryDataStoreException.class)
	public void findChineseShouldThrowExceptionWhenQueryFails() throws Exception {
		when(dictionaryEntryDao.query(anyDictionaryEntryPreparedQuery(), isA(CancellationSignaller.class))).thenThrow(new SQLException());
		dictionaryDataStore.findChinese("好", 25, 0, new SearchCanceller());
	}

	@Test(expected = DictionaryDataStoreQueryCancelledException.class)
	public void findChineseShouldThrowExceptionWhenQueryCancelled() throws Exception {
		when(dictionaryEntryDao.query(anyDictionaryEntryPreparedQuery(), isA(CancellationSignaller.class))).thenThrow(
				new SQLException("ORMLITE: query cancelled"));
		dictionaryDataStore.findChinese("好", 25, 0, new SearchCanceller());
	}

	@Test
	public void populateMetadataShouldSaveCurrentFormatVersionToMetadataTable() throws Exception {
		ArgumentCaptor<DictionaryDataStoreMetadata> captor = ArgumentCaptor.forClass(DictionaryDataStoreMetadata.class);
		this.dictionaryDataStore.populateMetadata();
		verify(dictionaryMetadataDao).createOrUpdate(captor.capture());
		DictionaryDataStoreMetadata metadata = captor.getValue();
		assertThat(metadata.getId(), is(1L));
		assertThat(metadata.getVersion(), is(DictionaryDataStore.DATA_FORMAT_VERSION));
	}

	@Test
	public void currentFormatVersionShouldRetrieveVersionFromMetadataTable() throws Exception {
		final DictionaryDataStoreMetadata metadata = Mockito.mock(DictionaryDataStoreMetadata.class);
		when(metadata.getVersion()).thenReturn(5);
		when(dictionaryMetadataDao.queryForId(1L)).thenReturn(metadata);
		assertThat(this.dictionaryDataStore.getCurrentDataFormatVersion(), equalTo(5));
	}

}
