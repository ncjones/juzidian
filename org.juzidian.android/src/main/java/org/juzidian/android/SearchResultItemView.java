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
import org.juzidian.core.PinyinSyllable;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.RelativeLayout;
import android.widget.TextView;

/**
 * Display a dictionary entry as a search result.
 */
public class SearchResultItemView extends RelativeLayout {

	public SearchResultItemView(final Context context, final DictionaryEntry entry) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.search_result_item, this, true);
		final String pinyinDisplay = this.createPinyinDisplay(entry);
		final String englishDefinitionDisplay = this.createEnglishDefinitionDisplay(entry);
		this.getHanziTextView().setText(entry.getSimplified());
		this.getPinyiniTextView().setText(pinyinDisplay);
		this.getDefinitionTextView().setText(englishDefinitionDisplay);
	}

	private TextView getHanziTextView() {
		return (TextView) this.findViewById(R.id.textHanzi);
	}

	private TextView getPinyiniTextView() {
		return (TextView) this.findViewById(R.id.textPinyin);
	}

	private TextView getDefinitionTextView() {
		return (TextView) this.findViewById(R.id.textDefinition);
	}

	private String createEnglishDefinitionDisplay(final DictionaryEntry chineseWord) {
		final List<String> definitions = chineseWord.getDefinitions();
		final StringBuilder stringBuilder = new StringBuilder();
		for (final String definition : definitions) {
			stringBuilder.append(definition).append("; ");
		}
		return stringBuilder.substring(0, stringBuilder.length() - 2);
	}

	private String createPinyinDisplay(final DictionaryEntry chineseWord) {
		final StringBuilder stringBuilder = new StringBuilder();
		for (final PinyinSyllable syllable : chineseWord.getPinyin()) {
			stringBuilder.append(syllable.getDisplayValue()).append(" ");
		}
		return stringBuilder.toString().trim();
	}

}
