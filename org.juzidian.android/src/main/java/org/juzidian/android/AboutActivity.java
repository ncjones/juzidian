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

import javax.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.Html;
import android.text.format.DateFormat;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

/**
 * Activity for displaying the "about" page.
 */
public class AboutActivity extends RoboActivity {

	@InjectView(R.id.appVersion)
	private TextView appVersionTextView;

	@InjectView(R.id.buildDate)
	private TextView buildDateTextView;

	@InjectView(R.id.headCommit)
	private TextView headCommitTextView;

	@InjectView(R.id.dictionaryDataDisclaimer)
	private TextView dictionaryDataDisclaimerTextView;

	@Inject
	private BuildInfo buildInfo;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_about);
		this.appVersionTextView.setText(this.getString(R.string.about_version, this.getVersionName()));
		this.buildDateTextView.setText(this.getString(R.string.about_build_date, this.getBuildDate()));
		this.headCommitTextView.setText(this.getString(R.string.about_source_id, this.getSourceId()));
		this.dictionaryDataDisclaimerTextView.setText(Html.fromHtml(this.getString(R.string.about_data_disclaimer_html)));
		this.dictionaryDataDisclaimerTextView.setMovementMethod(LinkMovementMethod.getInstance());
	}

	private String getVersionName() {
		try {
			return this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (final NameNotFoundException e) {
			throw new RuntimeException("Failed to get version name", e);
		}
	}

	private String getBuildDate() {
		final String timestamp = DateFormat.getDateFormat(this).format(this.buildInfo.getBuildDate());
		return timestamp;
	}

	private String getSourceId() {
		return this.buildInfo.getHeadCommit().substring(0, 10);
	}

}
