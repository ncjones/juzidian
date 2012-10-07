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

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import org.juzidian.core.datastore.JdbcDictionaryDataStore;

public class JdbcDictionaryModule extends DictionaryModule<JdbcDictionaryDataStore> {

	@Override
	protected Class<JdbcDictionaryDataStore> getDictionaryDataStoreClass() {
		return JdbcDictionaryDataStore.class;
	}

	@Override
	protected void configureAdditionalDependencies() {
		final Connection connection;
		try {
			Class.forName("org.sqlite.JDBC");
			connection = DriverManager.getConnection("jdbc:sqlite:juzidian.db");
		} catch (final ClassNotFoundException e) {
			throw new RuntimeException(e);
		} catch (final SQLException e) {
			throw new RuntimeException(e);
		}
		this.bind(Connection.class).toInstance(connection);
	}

}
