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
 * along with Juzidian.  If not, see &lt;http://www.gnu.org/licenses/>.
 */
package org.juzidian.android;

import android.app.Activity;
import android.os.Bundle;
import android.webkit.WebView;

public class AboutLicensesActivity extends Activity {

	private WebView webview;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.webview = new WebView(this);
		this.webview.loadUrl("file:///android_asset/licenses.html");
		this.setContentView(this.webview);
	}

	@Override
	public void onBackPressed() {
		if (this.webview.canGoBack()) {
			this.webview.goBack();
		} else {
			super.onBackPressed();
		}
	}

}
