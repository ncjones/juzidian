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

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

import javax.inject.Inject;
import javax.inject.Provider;

import android.content.Context;

/**
 * Provides a java.util.Properties for the build_info resource.
 */
public class BuildInfoPropertiesProvider implements Provider<Properties> {

	private final Context context;

	@Inject
	private BuildInfoPropertiesProvider(final Context context) {
		this.context = context;
	}

	@Override
	public Properties get() {
		final InputStream buildInfoInputStream = this.context.getResources().openRawResource(R.raw.build_info);
		return this.loadProperties(buildInfoInputStream);
	}

	private Properties loadProperties(final InputStream propertiesInputStream) {
		final Properties buildProperties = new Properties();
		try {
			buildProperties.load(propertiesInputStream);
		} catch (final IOException e) {
			throw new RuntimeException("Failed to load properties", e);
		}
		return buildProperties;
	}

}
