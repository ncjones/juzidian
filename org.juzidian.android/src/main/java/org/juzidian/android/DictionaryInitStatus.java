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


/**
 * Enumeration of the initialization states for a dictionary database.
 * <p>
 * The states, in sequence, are: {@link #UNINITIALIZED}, {@link #DOWNLOADING},
 * {@link #INSTALLING} and {@link #INITIALIZED}.
 */
public enum DictionaryInitStatus {

	/**
	 * A dictionary has not been initialized.
	 */
	UNINITIALIZED,

	/**
	 * Initialization is in progress - dictionary data is being downloaded.
	 */
	DOWNLOADING(true),

	/**
	 * Initialization is in progress - dictionary data is being installed.
	 */
	INSTALLING(true),

	/**
	 * A dictionary has been successfully initialized.
	 */
	INITIALIZED;

	private boolean inProgress;

	private DictionaryInitStatus() {
	}

	private DictionaryInitStatus(final boolean inProgress) {
		this.inProgress = inProgress;
	}

	/**
	 * Check if this status is an "in progress" status ({@link #DOWNLOADING} or
	 * {@link #INSTALLING}).
	 * 
	 * @return <code>true</code> if this status is "in progress".
	 */
	public boolean isInProgress() {
		return this.inProgress;
	}

}
