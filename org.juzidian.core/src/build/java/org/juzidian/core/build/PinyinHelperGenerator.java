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
package org.juzidian.core.build;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.text.DateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Set;

import org.juzidian.cedict.CedictLoader;

public class PinyinHelperGenerator {

	public static void main(final String[] args) throws IOException {
		if (args.length < 1) {
			System.out.println("CEDict input file must be specified.");
			return;
		}
		if (args.length < 2) {
			System.out.println("Java output file must be specified.");
			return;
		}
		if (args.length < 3) {
			System.out.println("Output package must be specified.");
			return;
		}
		final String cedictFile = args[0];
		final String outputDir = args[1];
		final String packageName = args[2];
		if (!new File(cedictFile).exists()) {
			System.out.println("Input file does not exist: " + cedictFile);
			return;
		}
		if (!isValidPackageName(packageName)) {
			System.out.println("Package name is invalid.");
			return;
		}
		final Set<String> uniquePinyin = getUniquePinyinSyllables(cedictFile);
		final String templateText = loadTemplate("PinyinHelper.java.template");
		final String templateOutput = templateText.replace("{{pinyinSyllablesArray}}", toArrayLiteral(uniquePinyin))
				.replace("{{package}}", packageName).replace("{{warning}}", "THIS FILE IS GENERATED - DO NOT MODIFY")
				.replace("{{date}}", DateFormat.getDateTimeInstance().format(new Date()));
		final String packageDir = packageName.replace('.', '/');
		final String outputFile = outputDir + "/" + packageDir + "/PinyinHelper.java";
		writeToFile(outputFile, templateOutput);
		System.out.println("Generated Java source: " + outputFile);
	}

	private static boolean isValidPackageName(final String packageName) {
		for (final String packagePart : packageName.split("\\.")) {
			if (!isValidIdentifier(packagePart)) {
				return false;
			}
		}
		return true;
	}

	private static boolean isValidIdentifier(final String identifier) {
		if (!Character.isJavaIdentifierStart(identifier.charAt(0))) {
			return false;
		}
		for (int i = 0; i < identifier.length(); i++) {
			if (!Character.isJavaIdentifierPart(identifier.charAt(i))) {
				return false;
			}
		}
		return true;
	}

	private static void writeToFile(final String fileName, final String text) throws IOException {
		new File(fileName).getParentFile().mkdirs();
		final OutputStream outputStream = new FileOutputStream(fileName);
		outputStream.write(text.getBytes());
		outputStream.close();
	}

	private static String toArrayLiteral(final Collection<String> strings) {
		final StringBuilder builder = new StringBuilder();
		builder.append("{\n\t\t\t");
		for (final String string : strings) {
			builder.append("\"").append(string).append("\"").append(",");
			final int lineLength = builder.length() - builder.lastIndexOf("\n");
			if (lineLength > 80) {
				builder.append("\n\t\t\t");
			}
		}
		builder.append("\n\t\t}");
		return builder.toString();
	}

	private static String loadTemplate(final String fileName) throws IOException {
		final BufferedReader reader = new BufferedReader(new InputStreamReader(PinyinHelperGenerator.class.getClassLoader().getResourceAsStream(
				fileName)));
		final StringBuilder builder = new StringBuilder();
		String line = reader.readLine();
		while (line != null) {
			builder.append(line).append("\n");
			line = reader.readLine();
		}
		return builder.toString();
	}

	private static Set<String> getUniquePinyinSyllables(final String cedictFile) throws IOException {
		final CedictLoader cedictFileLoader = new CedictLoader();
		final InputStream inputStream = new FileInputStream(cedictFile);
		final PinyinSyllableCaptor pinyinCaptor = new PinyinSyllableCaptor();
		cedictFileLoader.loadEntries(inputStream, pinyinCaptor);
		return pinyinCaptor.getUniquePinyinSyllables();
	}
}
