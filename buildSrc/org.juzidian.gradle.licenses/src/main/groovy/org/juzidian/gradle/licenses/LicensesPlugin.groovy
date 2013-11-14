package org.juzidian.gradle.licenses

import java.io.FileOutputStream

import org.gradle.api.Plugin
import org.gradle.api.Project

class LicensesPlugin implements Plugin<Project> {

	def retrieveLicenseTaskName(license) {
		"retrieveLicense-$license.name"
	}

	def loadLicenseTextTaskName(license) {
		"loadLicenseText-$license.name"
	}

	def initializeLicenseTaskName(license) {
		"initializeLicense-$license.name"
	}

	def createHtml(text) {
		return text
		.replaceAll('<', '&lt;')
		.replaceAll('\n\n', '\n<br><br>\n')
	}

	def createLicenseTasks(project, license) {
		if (!license.text) {
			def licensesDir = project.file("$project.buildDir/licenses")
			def licenseFile = project.file("$licensesDir/${license.name}.txt")
			project.task(retrieveLicenseTaskName(license)) {
				def licenseUrl = license.textUrl
				inputs.property 'url', licenseUrl
				outputs.file licenseFile
				doLast {
					licensesDir.mkdirs()
					def outputStream = new FileOutputStream(licenseFile)
					println "Downloading license text from $licenseUrl"
					new URL(licenseUrl).withInputStream { outputStream << it }
				}
			}
			project.task(loadLicenseTextTaskName(license)) {
				dependsOn retrieveLicenseTaskName(license)
				doLast {
					license.text = licenseFile.text
				}
			}
		}
		project.task(initializeLicenseTaskName(license)) {
			if (!license.text) {
				dependsOn loadLicenseTextTaskName(license)
			}
			doLast {
				license.html = createHtml(license.text)
			}
		}
	}

	void apply(final Project project) {
		def licenses = project.container(License)
		project.task('initializeLicenses')
		project.afterEvaluate {
			licenses.each {
				createLicenseTasks(project, it)
				project.tasks.initializeLicenses.dependsOn initializeLicenseTaskName(it)
			}
		}
		project.extensions.licenses = licenses
	}
}
