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

import org.juzidian.core.dataload.DictionaryResource;

/**
 * Service for downloading and installing dictionaries.
 */
public interface DictionaryInitService {

	/**
	 * Check if the dictionary is initialized.
	 * 
	 * @return <code>true</code> if the dictionary database is initialized.
	 */
	boolean isDictionaryInitialized();

	/**
	 * Check if dictionary initialization is in progress.
	 * 
	 * @return <code>true</code> if the dictionary database is in the process of
	 *         being initialized.
	 */
	boolean isInitializationInProgress();

	/**
	 * Schedules the asynchronous download and installation of a dictionary.
	 * <p>
	 * The download will be run on a background thread. Any attached
	 * {@link DictionaryInitListener} will be notified of significant events.
	 * 
	 * @throws IllegalStateException if there is already a download in progress.
	 */
	void downloadDictionary(final DictionaryResource dictionaryResource);

	/**
	 * Add a {@link DictionaryInitListener} to receive download
	 * notifications of download events.
	 * 
	 * @param downloadListener the listener to receive notifications.
	 */
	void addListener(final DictionaryInitListener downloadListener);

	/**
	 * Remove a {@link DictionaryInitListener} so it no longer receives
	 * download events.
	 * 
	 * @param downloadListener the listener to no longer receive notifications.
	 */
	void removeListener(final DictionaryInitListener downloadListener);

}
