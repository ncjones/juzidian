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

import java.sql.SQLException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.Callable;

import javax.inject.Inject;

import org.juzidian.core.DictionaryDataStore;
import org.juzidian.core.DictionaryEntry;
import org.juzidian.core.PinyinSyllable;
import org.juzidian.core.Tone;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.misc.TransactionManager;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.support.ConnectionSource;

public class DbDictionaryDataStore implements DictionaryDataStore {

	private static final Logger LOGGER = LoggerFactory.getLogger(DbDictionaryDataStore.class);

	private static final Long METADATA_ROW_ID = 1L;

	private static final int DATA_FORMAT_VERSION_NUMBER = 0;

	private final Dao<DbDictionaryEntry, Long> dictionaryEntryDao;

	private final Dao<DbDictionaryMetadata, Long> dictionaryMetadataDao;

	@Inject
	public DbDictionaryDataStore(final Dao<DbDictionaryEntry, Long> dictionaryEntryDao,
			final Dao<DbDictionaryMetadata, Long> dictionaryMetadataDao) {
		this.dictionaryEntryDao = dictionaryEntryDao;
		this.dictionaryMetadataDao = dictionaryMetadataDao;
	}

	/**
	 * Create or re-create the database schema.
	 * <p>
	 * This operation will destroy all existing data.
	 */
	public void createSchema() {
		new DbDictionaryDataStoreSchemaCreator().createSchema(this.getConnectionSource());
	}

	private ConnectionSource getConnectionSource() {
		return this.dictionaryEntryDao.getConnectionSource();
	}

	/**
	 * Insert expected dictionary metadata into the database.
	 */
	public void populateMetadata() {
		LOGGER.debug("Populating DB metadata.");
		final DbDictionaryMetadata metadata = new DbDictionaryMetadata();
		metadata.setId(METADATA_ROW_ID);
		metadata.setVersion(DATA_FORMAT_VERSION_NUMBER);
		metadata.setBuildDate(new Date());
		this.saveMetadata(metadata);
	}

	private void saveMetadata(final DbDictionaryMetadata metadata) {
		LOGGER.debug("Saving DB metadata: {}.", metadata);
		try {
			this.dictionaryMetadataDao.createOrUpdate(metadata);
		} catch (final SQLException e) {
			throw new DictionaryDataStoreException("Failed to create metadata", e);
		}
	}

	@Override
	public void add(final Collection<DictionaryEntry> entries) {
		LOGGER.debug(String.format("Adding %d entries to dictionary DB.", entries.size()));
		try {
			TransactionManager.callInTransaction(this.dictionaryEntryDao.getConnectionSource(), new BulkEntryAdd(entries));
		} catch (final SQLException e) {
			throw new DictionaryDataStoreException("Failed to add dictionary entries", e);
		}
	}

	private class BulkEntryAdd implements Callable<Void> {

		private final Collection<DictionaryEntry> entries;

		public BulkEntryAdd(final Collection<DictionaryEntry> entries) {
			this.entries = entries;
		}

		@Override
		public Void call() throws Exception {
			for (final DictionaryEntry dictionaryEntry : this.entries) {
				DbDictionaryDataStore.this.add(dictionaryEntry);
			}
			return null;
		}

	}

	public void add(final DictionaryEntry entry) {
		LOGGER.debug("Adding entry to dictionary DB: " + entry);
		final DbDictionaryEntry dbEntry = this.createDbEntry(entry);
		try {
			this.dictionaryEntryDao.create(dbEntry);
		} catch (final SQLException e) {
			throw new DictionaryDataStoreException("Failed to add dictionary entry: " + entry, e);
		}
	}

	private DbDictionaryEntry createDbEntry(final DictionaryEntry entry) {
		final DbDictionaryEntry dbEntry = new DbDictionaryEntry();
		dbEntry.setTraditional(entry.getTraditional());
		dbEntry.setSimplified(entry.getSimplified());
		dbEntry.setPinyin(this.formatPinyin(entry.getPinyin()));
		dbEntry.setEnglish(this.formatDefinitions(entry.getDefinitions()));
		return dbEntry;
	}

	private String formatPinyin(final List<PinyinSyllable> list) {
		final StringBuilder sb = new StringBuilder();
		for (final PinyinSyllable pinyinSyllable : list) {
			/*
			 * Add a leading space so that "pinyin contains" searches do not get
			 * false matches on similar pinyin syllables. For example, '*hao*'
			 * should not match 'zhao'.
			 */
			sb.append(" ").append(pinyinSyllable.getLetters()).append(pinyinSyllable.getTone().getNumber());
		}
		/*
		 * Add trailing space so that exact pinyin syllables can be
		 * distinguished in "like" query (and ordered accordingly). For example,
		 * "han*" should match all "han" syllables before matching any "hang"
		 * syllable.
		 */
		sb.append(" ");
		return sb.toString();
	}

	private String formatPinyinQuery(final List<PinyinSyllable> pinyinSyllables) {
		final StringBuilder sb = new StringBuilder();
		for (final PinyinSyllable pinyinSyllable : pinyinSyllables) {
			final Tone tone = pinyinSyllable.getTone();
			/* Use underscore to match "any" tone in an SQL "like" query. */
			final String toneSearchValue = Tone.ANY.equals(tone) ? "_" : tone.getNumber().toString();
			sb.append(" ").append(pinyinSyllable.getLetters()).append(toneSearchValue);
		}
		return sb.toString();
	}

	private List<PinyinSyllable> unformatPinyin(final String pinyin) {
		final String[] rawPinyin = pinyin.trim().split(" ");
		final List<PinyinSyllable> syllables = new LinkedList<PinyinSyllable>();
		for (final String letters : rawPinyin) {
			final PinyinSyllable syllable = this.parseSyllable(letters);
			syllables.add(syllable);
		}
		return syllables;
	}

	private PinyinSyllable parseSyllable(final String formattedPinyinSyllable) {
		final String pinyinLetters = formattedPinyinSyllable.substring(0, formattedPinyinSyllable.length() - 1);
		final int pinyinToneNumber = Integer.parseInt(formattedPinyinSyllable.substring(formattedPinyinSyllable.length() - 1));
		return new PinyinSyllable(pinyinLetters, Tone.valueOf(pinyinToneNumber));
	}

	private String formatDefinitions(final List<String> definitions) {
		final StringBuilder sb = new StringBuilder("/");
		for (final String definition : definitions) {
			sb.append(" ").append(definition.trim()).append(" /");
		}
		return sb.toString();
	}

	private List<String> unformatDefinitions(final String english) {
		final String[] definitions = english.substring(2, english.length() - 2).split(" / ");
		return Arrays.asList(definitions);
	}

	@Override
	public List<DictionaryEntry> findPinyin(final List<PinyinSyllable> pinyin, final long limit, final long offset) {
		if (limit < 0) {
			throw new IllegalArgumentException("Invalid limit: " + limit);
		}
		if (offset < 0) {
			throw new IllegalArgumentException("Invalid offset: " + offset);
		}
		LOGGER.debug("Finding pinyin: " + pinyin);
		try {
			final String pinyinQueryString = this.formatPinyinQuery(pinyin);
			final PreparedQuery<DbDictionaryEntry> query = this.dictionaryEntryDao.queryBuilder()
					.orderByRaw("case when like ('" + pinyinQueryString + " %', " + DbDictionaryEntry.COLUMN_PINYIN + ") " +
								"then 1 else 0 end desc, " +
							"length(" + DbDictionaryEntry.COLUMN_HANZI_SIMPLIFIED + "), " +
							DbDictionaryEntry.COLUMN_PINYIN)
					.limit(limit)
					.offset(offset)
					.where().like(DbDictionaryEntry.COLUMN_PINYIN, pinyinQueryString + "%")
					.prepare();
			return this.transformEntries(this.dictionaryEntryDao.query(query));
		} catch (final SQLException e) {
			throw new DictionaryDataStoreException("Failed to execute query", e);
		}
	}

	private List<DictionaryEntry> transformEntries(final List<DbDictionaryEntry> dbEntries) {
		final List<DictionaryEntry> entries = new LinkedList<DictionaryEntry>();
		for (final DbDictionaryEntry dbEntry : dbEntries) {
			entries.add(this.createEntry(dbEntry));
		}
		return entries;
	}

	private DictionaryEntry createEntry(final DbDictionaryEntry dbEntry) {
		final String traditional = dbEntry.getTraditional();
		final String simplified = dbEntry.getSimplified();
		final String pinyin = dbEntry.getPinyin();
		final String english = dbEntry.getEnglish();
		return new BasicDictionaryEntry(traditional, simplified, this.unformatPinyin(pinyin), this.unformatDefinitions(english));
	}

	@Override
	public List<DictionaryEntry> findChinese(final String chineseCharacters, final long limit, final long offset) {
		if (limit < 0) {
			throw new IllegalArgumentException("Invalid limit: " + limit);
		}
		if (offset < 0) {
			throw new IllegalArgumentException("Invalid offset: " + offset);
		}
		LOGGER.debug("Finding Chinese characters: " + chineseCharacters);
		try {
			final PreparedQuery<DbDictionaryEntry> query = this.dictionaryEntryDao.queryBuilder()
					.orderByRaw("case " +
							"when like ('" + chineseCharacters + "%', " + DbDictionaryEntry.COLUMN_HANZI_SIMPLIFIED + ") then 0 " +
							"else 1 end, " +
						"length(" + DbDictionaryEntry.COLUMN_HANZI_SIMPLIFIED + "), " +
						DbDictionaryEntry.COLUMN_PINYIN)
					.limit(limit)
					.offset(offset)
					.where().like(DbDictionaryEntry.COLUMN_HANZI_SIMPLIFIED, "%" + chineseCharacters + "%")
					.prepare();
			return this.transformEntries(this.dictionaryEntryDao.query(query));
		} catch (final SQLException e) {
			throw new DictionaryDataStoreException("Failed to execute query", e);
		}
	}

	@Override
	public List<DictionaryEntry> findDefinitions(final String englishWords, final long limit, final long offset) {
		if (limit < 0) {
			throw new IllegalArgumentException("Invalid limit: " + limit);
		}
		if (offset < 0) {
			throw new IllegalArgumentException("Invalid offset: " + offset);
		}
		LOGGER.debug("Finding definitions: " + englishWords);
		try {
			final PreparedQuery<DbDictionaryEntry> query = this.dictionaryEntryDao.queryBuilder()
					.orderByRaw("case " +
								"when like ('/ " + englishWords + " /%', " + DbDictionaryEntry.COLUMN_ENGLISH + ") then 0 " +
								"when like ('%/ " + englishWords + " /%', " + DbDictionaryEntry.COLUMN_ENGLISH + ") then 1 " +
								"when like ('%/ " + englishWords + " %', " + DbDictionaryEntry.COLUMN_ENGLISH + ") then 2 " +
								"when like ('% " + englishWords + " %', " + DbDictionaryEntry.COLUMN_ENGLISH + ") then 3 " +
								"else 4 end, " +
							"length(" + DbDictionaryEntry.COLUMN_HANZI_SIMPLIFIED + "), " +
							DbDictionaryEntry.COLUMN_PINYIN)
					.limit(limit)
					.offset(offset)
					.where().like(DbDictionaryEntry.COLUMN_ENGLISH, "%" + englishWords + "%")
					.prepare();
			return this.transformEntries(this.dictionaryEntryDao.query(query));
		} catch (final SQLException e) {
			throw new DictionaryDataStoreException("Failed to execute query", e);
		}
	}

}
