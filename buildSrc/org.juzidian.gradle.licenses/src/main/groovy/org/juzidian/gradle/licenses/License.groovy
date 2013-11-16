package org.juzidian.gradle.licenses;

class License {
	String name
	String title
	String textUrl
	String text
	String html
	List<LicensedComponent> components

	License(String name) {
		this.name = name;
	}
}