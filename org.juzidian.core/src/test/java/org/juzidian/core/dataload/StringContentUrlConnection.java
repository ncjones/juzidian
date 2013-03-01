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

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.URLConnection;

public class StringContentUrlConnection extends URLConnection {

	private final String content;

	public StringContentUrlConnection(final String content) {
		super(null);
		this.content = content;
	}

	@Override
	public void connect() throws IOException {

	}

	@Override
	public InputStream getInputStream() throws IOException {
		return new ByteArrayInputStream(this.content.getBytes());
	}

}