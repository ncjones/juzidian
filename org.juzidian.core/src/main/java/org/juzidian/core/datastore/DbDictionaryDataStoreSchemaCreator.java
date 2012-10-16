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

import org.juzidian.cedict.CedictLoader;

import com.j256.ormlite.dao.Dao;

/**
 * Creates {@link DbDictionaryDataStore} databases from a {@link CedictLoader}.
 */
public class DbDictionaryDataStoreSchemaCreator {

	/**
	 * Create or re-create a {@link DbDictionaryDataStore} DB schema using the
	 * given OrmLite DAO.
	 */
	public void createSchema(final Dao<DbDictionaryEntry, Long> ormLiteDao) {
		try {
			ormLiteDao.executeRawNoArgs("drop table if exists entry");
			ormLiteDao.executeRawNoArgs("create table entry (" +
					"id string," +
					"hanzi_traditional string," +
					"hanzi_simplified string," +
					"pinyin string," +
					"english string" +
					")");
		} catch (final SQLException e) {
			throw new RuntimeException("Failed to created tables", e);
		}
	}

}
