About
=====

Juzidian is a Chinese-English dictionary for Android, targeted at English-
speaking learners of Mandarin Chinese.

Juzidian is free software, licensed under the GPLv3.

See <http://www.juzidian.org/> for more details.


Build Dependencies
==================

 * Java 1.7 JDK
 * Gradle 1.6
 * Android SDK (Platform API 16, Platform-tools 16.0.2, Build Tools 17)
 * Git (rev-parse)
 * WenQuanYi Zen Hei font
 * Maven 3.1


Building
========

 * Initialise submodules for Ormlite fork:

	$ git submodule init;\
	git submodule update
	
 * Install Ormlite fork to local mvn repository:

	$ mvn -f ormlite-core/pom.xml clean install;\
	mvn -f ormlite-jdbc/pom.xml clean install;\
	mvn -f ormlite-android/pom.xml clean install

 * Create a file called 'local.properties' and set the 'sdk.dir' property to the
   location of your Android SDK installation directory, eg:

	$ echo "sdk.dir=$HOME/android-sdk-linux" > local.properties

 * Build Juzidian APK:

	$ gradle assembleDebug


Releasing
=========

To build a signed release APK, define the "ANDROID_KEYSTORE" environment
variable to point to a Java keystore file and run:

	$ gradle assembleRelease

You will be prompted for keystore and key pass phrases.


Installing
==========

Install the built APK on to a connected Android device:

	$ gradle installDebug

or

	$ gradle installRelease


Installing Data
===============

Dictionary data will be downloaded automatically when Juzidian starts up. To
skip this download step the data can be manually built and pushed to the device.
See org.juzidian.build.datagen/README for details about dictionary data
generation and publishing. Once dictionary data has been generated, copy it to a
connected Android device with:

	$ adb push org.juzidian.build.datagen/build/juzidian_dictionary.db \
	/data/data/org.juzidian.android/juzidian-dictionary.db

Note: Data installation requires adb root access on the device.


Build Profiles
==============

Build profiles allow easy switching between different build configurations. Each
build profile is defined in a Gradle script with a .profile extension. To build
using a specific build profile, set the "profile" property via a Gradle "-P"
argument. If no profile is specified then the "default" profile is used.

For example, to build the Juzidian APK using the "dev" profile, use:

	$ gradle -Pprofile=dev build


IDE Setup
=========

To setup Eclipse projects with correct natures and class paths, run

	$ gradle eclipse;\
	gradle -p buildSrc eclipse;\
	gradle -c datagen-settings.gradle eclipse

and then import all projects into Eclipse from the project root directory.

