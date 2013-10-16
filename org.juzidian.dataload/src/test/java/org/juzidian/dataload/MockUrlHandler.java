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
package org.juzidian.dataload;

import java.io.IOException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLStreamHandler;

import org.skife.url.UrlSchemeRegistry;

public class MockUrlHandler extends URLStreamHandler {

	static {
		UrlSchemeRegistry.register("mock", MockUrlHandler.class);
	}

	public static MockUrlHandler delegate;

	@Override
	public URLConnection openConnection(final URL url) throws IOException {
		return MockUrlHandler.delegate.openConnection(url);
	}

}

