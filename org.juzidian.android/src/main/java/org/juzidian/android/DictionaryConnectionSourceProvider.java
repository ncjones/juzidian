/*
 * Copyright Nathan Jones 2013
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
package org.juzidian.android;

import static android.database.sqlite.SQLiteDatabase.NO_LOCALIZED_COLLATORS;
import static android.database.sqlite.SQLiteDatabase.OPEN_READONLY;

import javax.inject.Provider;

import android.database.sqlite.SQLiteDatabase;

import com.j256.ormlite.android.AndroidConnectionSource;
import com.j256.ormlite.support.ConnectionSource;

public class DictionaryConnectionSourceProvider implements Provider<ConnectionSource> {

	private static final String DICTIONARY_DB_PATH = "/data/data/org.juzidian.android/juzidian-dictionary.db";

	@Override
	public ConnectionSource get() {
		final SQLiteDatabase sqliteDb = SQLiteDatabase.openDatabase(DICTIONARY_DB_PATH, null, OPEN_READONLY | NO_LOCALIZED_COLLATORS);
		final ConnectionSource connectionSource = new AndroidConnectionSource(sqliteDb);
		return connectionSource;
	}

}
