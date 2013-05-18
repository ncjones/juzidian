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
import javax.inject.Singleton;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;

/**
 * A persistent registry for a currently active dictionary data download.
 */
@Singleton
public class DownloadRegistry {

	private static final Logger LOGGER = LoggerFactory.getLogger(DownloadRegistry.class);

	private static final String CURRENT_DOWNLOAD_ID_KEY = "current-download-id";

	private final SharedPreferences sharedPreferences;

	@Inject
	public DownloadRegistry(@DownloadSharedPrefs final SharedPreferences sharedPreferences) {
		this.sharedPreferences = sharedPreferences;
	}

	/**
	 * @return the id of the current active download or <code>null</code> if
	 *         there is none.
	 */
	public Long getCurrentDownloadId() {
		final String downloadIdString = this.sharedPreferences.getString(CURRENT_DOWNLOAD_ID_KEY, null);
		if (downloadIdString == null) {
			return null;
		}
		return Long.parseLong(downloadIdString);
	}

	/**
	 * Set the id of the current active download.
	 * <p>
	 * Any existing download id will be over written.
	 * 
	 * @param downloadId the id of the new current download or <code>null</code>
	 *        to clear the download id.
	 */
	public void setCurrentDownloadId(final Long downloadId) {
		LOGGER.debug("Setting current dictionary download id: {}", downloadId);
		final Editor editor = this.sharedPreferences.edit();
		if (downloadId == null) {
			editor.remove(CURRENT_DOWNLOAD_ID_KEY);
		} else {
			editor.putString(CURRENT_DOWNLOAD_ID_KEY, "" + downloadId);
		}
		editor.apply();
	}

}
