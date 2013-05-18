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

import javax.inject.Inject;

import org.juzidian.core.dataload.DictionaryResource;
import org.juzidian.core.dataload.DictionaryResourceRegistry;
import org.juzidian.core.dataload.DictionaryResourceRegistryService;
import org.juzidian.core.dataload.DictonaryResourceRegistryServiceException;
import org.juzidian.core.datastore.DbDictionaryDataStore;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.Uri;

/**
 * Triggers the download of a dictionary database file.
 */
public class DictionaryDownloader {

	private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryDownloader.class);

	/**
	 * The name of the preference property containing the currently active
	 * dictionary download id.
	 */
	public static final String CURRENT_DOWNLOAD_ID = "current-download-id";

	private final DownloadManager downloadManager;

	private final DictionaryResourceRegistryService registryService;

	private final SharedPreferences sharedPreferences;

	@Inject
	public DictionaryDownloader(final DownloadManager downloadManager, final DictionaryResourceRegistryService registryService,
			@DownloadSharedPrefs final SharedPreferences sharedPreferences) {
		this.downloadManager = downloadManager;
		this.registryService = registryService;
		this.sharedPreferences = sharedPreferences;
	}

	public void downloadDictionary() {
		LOGGER.debug("Initializing download of dictionary database.");
		final DictionaryResource dictionaryResource = this.getDictionaryResource();
		final Request request = new DownloadManager.Request(Uri.parse(dictionaryResource.getUrl()));
		request.setTitle("Juzidian Dictionary Database");
		final long downloadId = this.downloadManager.enqueue(request);
		final Editor editor = this.sharedPreferences.edit();
		editor.putLong(CURRENT_DOWNLOAD_ID, downloadId);
		editor.apply();
	}

	private DictionaryResource getDictionaryResource() {
		final DictionaryResourceRegistry registry;
		try {
			registry = this.registryService.getDictionaryResourceRegistry(DbDictionaryDataStore.DATA_FORMAT_VERSION);
		} catch (final DictonaryResourceRegistryServiceException e) {
			throw new RuntimeException(e);
		}
		return registry.getDictionaryResources().get(0);
	}

}
