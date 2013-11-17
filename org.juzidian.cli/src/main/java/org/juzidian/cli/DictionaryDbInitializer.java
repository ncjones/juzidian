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
package org.juzidian.cli;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;

import javax.inject.Inject;

import org.juzidian.core.DictionaryDataStore;
import org.juzidian.dataload.DictionaryResource;
import org.juzidian.dataload.DictionaryResourceDownloader;
import org.juzidian.dataload.DictionaryResourceRegistry;
import org.juzidian.dataload.DictionaryResourceRegistryService;
import org.juzidian.dataload.DictonaryResourceRegistryServiceException;
import org.juzidian.util.IoUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

class DictionaryDbInitializer {

	private static final Logger LOGGER = LoggerFactory.getLogger(DictionaryDbInitializer.class);

	final DictionaryDataStore dictionaryDataStore;

	final DictionaryResourceRegistryService dictionaryRegistryService;

	final DictionaryResourceDownloader dictionaryDownloader;

	final File dictionaryDbFile;

	@Inject
	public DictionaryDbInitializer(final DictionaryDataStore dataStore, final DictionaryResourceRegistryService registryService,
			final DictionaryResourceDownloader dictionaryDownloader, @DictionaryDbPath final File dictionaryDbFile) {
		this.dictionaryDataStore = dataStore;
		this.dictionaryRegistryService = registryService;
		this.dictionaryDownloader = dictionaryDownloader;
		this.dictionaryDbFile = dictionaryDbFile;
	}

	public void initializeDb() throws Exception {
		if (!this.dictionaryDbFile.exists()) {
			LOGGER.info("Dictionary DB missing.");
			this.downloadDb();
		} else {
			if (this.dictionaryDataStore.getCurrentDataFormatVersion() != DictionaryDataStore.DATA_FORMAT_VERSION) {
				LOGGER.info("Dictionary DB incompatible.");
				this.downloadDb();
			} else {
				LOGGER.debug("Dictionary DB compatible.");
			}
		}
	}

	private void downloadDb() throws Exception {
		this.dictionaryDbFile.getParentFile().mkdirs();
		final DictionaryResource resource = this.getDictionaryResource();
		LOGGER.info("Downloading dictionary DB: " + resource.getUrl());
		final File tempFile = File.createTempFile("juzidian-dictionary-download", null);
		this.dictionaryDownloader.download(resource, new FileOutputStream(tempFile), new DownloadProgressLogger());
		IoUtil.copy(new FileInputStream(tempFile), new FileOutputStream(this.dictionaryDbFile));
	}

	private DictionaryResource getDictionaryResource() throws DictonaryResourceRegistryServiceException {
		final DictionaryResourceRegistry registry = this.getDictionaryResourceRegistry();
		return registry.getDictionaryResources().get(0);
	}

	private DictionaryResourceRegistry getDictionaryResourceRegistry() throws DictonaryResourceRegistryServiceException {
		LOGGER.debug("Getting registry of available dictionary DBs.");
		return this.dictionaryRegistryService.getDictionaryResourceRegistry(DictionaryDataStore.DATA_FORMAT_VERSION);
	}

}