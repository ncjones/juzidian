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
package org.juzidian.core;

/**
 * An event source for triggering and notifying search cancellation events.
 */
public class SearchCanceller {

	private volatile Listener listener;

	/**
	 * Notify any registered {@link Listener} that search cancellation has been
	 * triggered.
	 */
	public void cancel() {
		if (listener != null) {
			listener.onCancel();
		}
	}

	/**
	 * Register a {@link Listener} to be notified when cancellation is
	 * triggered.
	 * 
	 * @param cancellationListener a {@link Listener}.
	 */
	public void register(final Listener listener) {
		this.listener = listener;
	}

	/**
	 * Listener for search query cancellation events.
	 */
	public static interface Listener {

		public void onCancel();

	}

}
