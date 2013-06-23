package org.juzidian.android;

import roboguice.activity.RoboActivity;
import roboguice.inject.InjectView;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.widget.TextView;

/**
 * Activity for displaying the "about" page.
 */
public class AboutActivity extends RoboActivity {

	@InjectView(R.id.appVersion)
	private TextView appVersionTextView;

	@Override
	protected void onCreate(final Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_about);
		this.appVersionTextView.setText(this.getString(R.string.about_version, this.getVersionName()));
	}

	private String getVersionName() {
		try {
			return this.getPackageManager().getPackageInfo(this.getPackageName(), 0).versionName;
		} catch (final NameNotFoundException e) {
			throw new RuntimeException("Failed to get version name", e);
		}
	}

}
