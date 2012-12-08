/*
 * Copyright Nathan Jones 2012
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

import org.juzidian.core.DictionaryEntry;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ProgressBar;

/**
 * A list adaptor for rendering search results.
 */
public class SearchResultsListAdapter extends ArrayAdapter<DictionaryEntry> {

	private boolean loading;

	public SearchResultsListAdapter(final Context context) {
		super(context, android.R.id.text1);
	}

	public void setLoading(final boolean loading) {
		this.loading = loading;
		super.notifyDataSetChanged();
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		if (this.loading && position == this.getCount() - 1) {
			return new ProgressBar(parent.getContext());
		}
		return new SearchResultItemView(parent.getContext(), this.getItem(position));
	}

	@Override
	public int getCount() {
		return super.getCount() + (this.loading ? 1 : 0);
	}

}
