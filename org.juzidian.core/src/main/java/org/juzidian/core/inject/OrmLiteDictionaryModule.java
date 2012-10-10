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
package org.juzidian.core.inject;

import java.sql.SQLException;

import org.juzidian.core.datastore.DbDictionaryDataStore;
import org.juzidian.core.datastore.DbDictionaryEntry;

import com.google.inject.TypeLiteral;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.dao.DaoManager;
import com.j256.ormlite.support.ConnectionSource;

public abstract class OrmLiteDictionaryModule extends DictionaryModule<DbDictionaryDataStore> {

	@Override
	protected Class<DbDictionaryDataStore> getDictionaryDataStoreClass() {
		return DbDictionaryDataStore.class;
	}

	@Override
	protected void configureAdditionalDependencies() {
		final ConnectionSource connectionSource;
		try {
			connectionSource = this.createConnectionSource("jdbc:sqlite:juzidian.db");
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
		final Dao<DbDictionaryEntry, Long> entryDao = this.createEntryDao(connectionSource);
		this.bind(new TypeLiteral<Dao<DbDictionaryEntry, Long>>() {}).toInstance(entryDao);
	}

	private Dao<DbDictionaryEntry, Long> createEntryDao(final ConnectionSource connectionSource) {
		try {
			return DaoManager.createDao(connectionSource, DbDictionaryEntry.class);
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
	}

	protected abstract ConnectionSource createConnectionSource(String jdbcUrl) throws SQLException;

}
