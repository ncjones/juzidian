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

import java.util.List;

import org.juzidian.core.DictionaryEntry;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;

/**
 * A list adaptor for rendering search results.
 */
public class SearchResultsListAdapter extends ArrayAdapter<DictionaryEntry> {

	public SearchResultsListAdapter(final Context context, final List<DictionaryEntry> entries) {
		super(context, android.R.id.text1, entries);
	}

	@Override
	public View getView(final int position, final View convertView, final ViewGroup parent) {
		return new SearchResultItemView(parent.getContext(), this.getItem(position));
	}

}
