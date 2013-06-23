package org.juzidian.android;

import javax.inject.Inject;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.widget.TextView;

/**
 * Activity for displaying the "about" page.
 */
public class AboutActivity extends RoboActivity {

	@InjectView(R.id.appVersion)
	private TextView appVersionTextView;

	@InjectView(R.id.buildId)
	private TextView buildIdTextView;

	@Inject
	private BuildInfo buildInfo;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_about);
		this.appVersionTextView.setText(this.getString(R.string.about_version, this.getVersionName()));
		this.buildIdTextView.setText(this.getString(R.string.about_build_id, this.getBuildId()));
	}

	private String getVersionName() {
		try {
			return this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (final NameNotFoundException e) {
			throw new RuntimeException("Failed to get version name", e);
		}
	}

	private String getBuildId() {
		final String timestamp = DateFormat.getDateFormat(this).format(this.buildInfo.getBuildDate());
		final String commit = this.buildInfo.getHeadCommit().substring(0, 10);
		return commit.substring(0, 10) + "-" + timestamp;
	}

}
