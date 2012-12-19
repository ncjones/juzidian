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

	private static final int VIEW_TYPE_SEARCH_RESULT = 0;

	private static final int VIEW_TYPE_LOADING_INDICATOR = 1;

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
		final int itemViewType = this.getItemViewType(position);
		switch (itemViewType) {
		case VIEW_TYPE_LOADING_INDICATOR:
			return new ProgressBar(parent.getContext());
		case VIEW_TYPE_SEARCH_RESULT:
			return this.getSearchResultItemView(position, (SearchResultItemView) convertView, parent);
		default:
			throw new IllegalStateException(String.format("Item at position %s has invalid view type: %s", position, itemViewType));
		}
	}

	private SearchResultItemView getSearchResultItemView(final int position, final SearchResultItemView convertView, final ViewGroup parent) {
		final DictionaryEntry entry = this.getItem(position);
		if (convertView != null) {
			convertView.setDictionaryEntry(entry);
			return convertView;
		}
		return new SearchResultItemView(parent.getContext(), entry);
	}

	@Override
	public int getCount() {
		return super.getCount() + (this.loading ? 1 : 0);
	}

	@Override
	public int getItemViewType(final int position) {
		if (this.loading && position == this.getCount() - 1) {
			return VIEW_TYPE_LOADING_INDICATOR;
		} else {
			return VIEW_TYPE_SEARCH_RESULT;
		}
	}

	@Override
	public int getViewTypeCount() {
		return 2;
	}

}
