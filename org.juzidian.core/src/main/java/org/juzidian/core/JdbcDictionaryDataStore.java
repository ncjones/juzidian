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

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.text.MessageFormat;
import java.util.Arrays;
import java.util.LinkedList;
import java.util.List;

import org.juzidian.cedict.CedictEntry;
import org.juzidian.cedict.CedictLoadHandler;
import org.juzidian.cedict.CedictLoader;

public class JdbcDictionaryDataStore implements DictionaryDataStore, CedictLoadHandler {

	private final Connection connection;

	private final CedictLoader cedictLoader;

	private int count;

	public JdbcDictionaryDataStore(final Connection connection, final CedictLoader cedictLoader) {
		this.connection = connection;
		this.cedictLoader = cedictLoader;
	}

	@Override
	public void initialize() {
		try {
			final Statement statement = this.connection.createStatement();
			statement.executeUpdate("drop table if exists entry");
			statement.executeUpdate("create table entry (hanzi_traditional string, hanzi_simplified string, " +
					"pinyin string, english string)");
			this.cedictLoader.loadEntries(this);
		} catch (final Exception e) {
			throw new DictionaryDataStoreException("Failed to initialise datastore", e);
		}
	}

	@Override
	public void loadingStarted() {
		this.count = 0;
		System.out.println("Loading started");
	}

	@Override
	public void entryLoaded(final CedictEntry cedictEntry) {
		this.count += 1;
		this.insert(new CedictDictionaryEntryAdaptor(cedictEntry));
	}

	@Override
	public void loadingFinished() {
		System.out.println("Loading completed");
	}

	public void insert(final DictionaryEntry entry) {
		System.out.println(MessageFormat.format("Inserting entry {0}: {1}", this.count, entry.toString()));
		try {
			final PreparedStatement statement = this.connection.prepareStatement("insert into entry values(?, ?, ?, ?)");
			statement.setString(1, entry.getTraditional());
			statement.setString(2, entry.getSimplified());
			// FIXME use suitable format for pinyin and english lists
			statement.setString(3, this.formatPinyin(entry.getPinyin()));
			statement.setString(4, this.formatDefinitions(entry.getDefinitions()));
			statement.execute();
		} catch (final SQLException e) {
			throw new DictionaryDataStoreException("Failed to insert entry: " + entry, e);
		}
	}

	private String formatPinyin(final List<PinyinSyllable> list) {
		final StringBuilder sb = new StringBuilder();
		for (final PinyinSyllable pinyinSyllable : list) {
			sb.append(pinyinSyllable.getLetters()).append(" ");
		}
		return sb.toString();
	}

	private List<PinyinSyllable> unformatPinyin(final String pinyin) {
		final String[] rawPinyin = pinyin.split("  ");
		final List<PinyinSyllable> syllables = new LinkedList<PinyinSyllable>();
		for (final String letters : rawPinyin) {
			final PinyinSyllable syllable = new PinyinSyllable(letters);
			syllables.add(syllable);
		}
		return syllables;
	}

	private String formatDefinitions(final List<String> definitions) {
		final StringBuilder sb = new StringBuilder();
		for (final String definition : definitions) {
			sb.append(definition).append("/");
		}
		return sb.toString();
	}

	private List<String> unformatDefinitions(final String english) {
		final String[] definitions = english.split("/");
		return Arrays.asList(definitions);
	}

	@Override
	public List<DictionaryEntry> findPinyin(final List<PinyinSyllable> pinyin) {
		final List<DictionaryEntry> entries = new LinkedList<DictionaryEntry>();
		try {
			final PreparedStatement statement = this.connection.prepareStatement("select * from entry where pinyin = ?");
			statement.setString(1, this.formatPinyin(pinyin));
			final ResultSet rs = statement.executeQuery();
			while (rs.next()) {
				final DictionaryEntry entry = this.createEntry(rs);
				entries.add(entry);
			}
			return entries;
		} catch (final SQLException e) {
			throw new DictionaryDataStoreException("Failed to execute query", e);
		}
	}

	private DictionaryEntry createEntry(final ResultSet rs) throws SQLException {
		final String traditional = rs.getString("hanzi_traditional");
		final String simplified = rs.getString("hanzi_simplified");
		final String pinyin = rs.getString("pinyin");
		final String english = rs.getString("english");
		return new BasicDictionaryEntry(traditional, simplified, this.unformatPinyin(pinyin), this.unformatDefinitions(english));
	}

	@Override
	public List<DictionaryEntry> findChinese(final String chineseCharacters) {
		// TODO Auto-generated method stub
		return null;
	}

	@Override
	public List<DictionaryEntry> findDefinitions(final String englishWords) {
		// TODO Auto-generated method stub
		return null;
	}

}
