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

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.zip.GZIPInputStream;

import org.juzidian.core.dataload.DictionaryResource;
import org.juzidian.core.dataload.DictionaryResourceRegistry;
import org.juzidian.core.dataload.DictionaryResourceRegistryService;
import org.juzidian.core.dataload.DictonaryResourceRegistryServiceException;
import org.juzidian.core.datastore.DbDictionaryDataStore;
import org.juzidian.util.IoUtil;

import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.IntentFilter;
import android.net.Uri;
import android.os.ParcelFileDescriptor;

/**
 * Initializes the dictionary database so it is ready to be read.
 */
public class DictionaryInitializer implements DownloadEventListener {

	public static final String DICTIONARY_DB_PATH = "/data/data/org.juzidian.android/juzidian-dictionary.db";

	private final Context context;

	private final DownloadManager downloadManager;

	private final DictionaryResourceRegistryService registryService;

	private final DictionaryInitializationListener listener;

	public DictionaryInitializer(final Context context, final DownloadManager downloadManager, final DictionaryResourceRegistryService registryService,
			final DictionaryInitializationListener listener) {
		this.context = context;
		this.downloadManager = downloadManager;
		this.registryService = registryService;
		this.listener = listener;
	}

	public void initializeDictionary() {
		if (new File(DICTIONARY_DB_PATH).exists()) {
			this.listener.initializationCompleted();
			return;
		}
		final DictionaryResource dictionaryResource = this.getDictionaryResource();
		final Request request = new DownloadManager.Request(Uri.parse(dictionaryResource.getUrl()));
		request.setTitle("Juzidian Dictionary Database");
		final long downloadId = this.downloadManager.enqueue(request);
		final BroadcastReceiver receiver = new DownloadBroadcastReceiver(downloadId, this);
		this.context.registerReceiver(receiver, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
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

	@Override
	public void downloadComplete(final DownloadBroadcastReceiver receiver, final long downloadId) {
		this.context.unregisterReceiver(receiver);
		final ParcelFileDescriptor fileDescriptor;
		try {
			fileDescriptor = this.downloadManager.openDownloadedFile(downloadId);
			final InputStream rawInputStream = new ParcelFileDescriptor.AutoCloseInputStream(fileDescriptor);
			final GZIPInputStream gzipInputStream = new GZIPInputStream(rawInputStream);
			IoUtil.copy(gzipInputStream, new FileOutputStream(DICTIONARY_DB_PATH));
			this.listener.initializationCompleted();
		} catch (final IOException e) {
			throw new RuntimeException(e);
		}
	}

}
