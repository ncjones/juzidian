package org.juzidian.gradle.licenses

import org.gradle.api.artifacts.Dependency
import org.gradle.api.artifacts.ModuleVersionIdentifier
import org.gradle.api.InvalidUserDataException
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

	def capitalize(s) {
		s[0].toUpperCase() + s.substring(1)
	}

	def dependencyHasModuleId(Dependency dependency, ModuleVersionIdentifier moduleId) {
		return moduleId.group == dependency.group &&
		moduleId.name == dependency.name &&
		moduleId.version == dependency.version
	}

	def verifyExternalDependenciesHaveLicenses(project, licensedComponents, licenses) {
		Collection<String> licensedArtifacts = licensedComponents.collect { it.artifacts }.flatten()
		Collection<Dependency> licensedArtifactDeps = licensedArtifacts.collect(project.dependencies.&create)
		project.configurations.compile.getIncoming().getResolutionResult().allDependencies {
			ModuleVersionIdentifier moduleId = it.selected.id
			if (moduleId.group != project.group && !(licensedArtifactDeps.any { dependencyHasModuleId(it, moduleId) })) {
				throw new InvalidUserDataException("$moduleId does not have a license reference")
			}
		}
	}

	void apply(final Project project) {
		def licenses = project.container(License)
		def licensedComponents = project.container(LicensedComponent)
		project.task('initializeLicenses')
		project.afterEvaluate {
			licenses.each {
				createLicenseTasks(project, it)
				project.tasks.initializeLicenses.dependsOn initializeLicenseTaskName(it)
				it.components = []
			}
			verifyExternalDependenciesHaveLicenses(project, licensedComponents, licenses)
			licensedComponents.each {
				it.title = it.title ?: capitalize(it.name)
				licenses.getByName(it.license).components += it
			}
		}
		project.extensions.licenses = licenses
		project.extensions.licensedComponents = licensedComponents
	}
}
