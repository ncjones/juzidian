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

import java.util.Properties;

import roboguice.inject.SharedPreferencesProvider;
import roboguice.inject.SystemServiceProvider;
import android.app.DownloadManager;
import android.content.Context;
import android.content.SharedPreferences;

import com.google.inject.AbstractModule;
import com.j256.ormlite.support.ConnectionSource;

public class JuzidianAndroidModule extends AbstractModule {

	@Override
	protected void configure() {
		this.bind(ConnectionSource.class).toProvider(DictionaryConnectionSourceProvider.class);
		this.bind(DownloadManager.class).toProvider(new SystemServiceProvider<DownloadManager>(Context.DOWNLOAD_SERVICE));
		this.bind(SharedPreferences.class).annotatedWith(DownloadSharedPrefs.class).toProvider(new SharedPreferencesProvider("juzidian-download-info"));
		this.bindConstant().annotatedWith(DictionaryDbPath.class).to("/data/data/org.juzidian.android/juzidian-dictionary.db");
		this.bind(Properties.class).toProvider(BuildInfoPropertiesProvider.class);
	}

}
