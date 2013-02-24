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
package org.juzidian.core.dataload;

class DictionaryResourceImpl implements DictionaryResource {

	private String url;

	private int size;

	private String sha1;

	@Override
	public String getUrl() {
		return this.url;
	}

	public void setUrl(final String url) {
		this.url = url;
	}

	@Override
	public int getSize() {
		return this.size;
	}

	public void setSize(final int size) {
		this.size = size;
	}

	@Override
	public String getSha1() {
		return this.sha1;
	}

	public void setSha1(final String sha1) {
		this.sha1 = sha1;
	}

}
