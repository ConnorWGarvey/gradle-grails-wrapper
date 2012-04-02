Gradle Grails Wrapper
=====================

This is a plugin for [Gradle](http://www.gradle.org) that builds [Grails](http://www.grails.org) applications.  It differs from gradle-grails-plugin in that it is much simpler so that, hopefully, it will be more functional.

Applying the plugin
-------------------

    buildscript {
      repositories {
        mavenCentral()
        maven { url 'http://connorwgarvey.github.com/gradle-grails-wrapper/repo/' }
      }

      dependencies {
        classpath 'com.connorgarvey.gradle:gradle-grails-wrapper:0.1'
      }
    }
    
    apply plugin: 'grails'

By default, the plugin will select a version of Grails.  To specify a version, add this to the build.

    grails {
      version '2.0.2' 
    }

Using the plugin
----------------

The plugin adds standard Grails build commands to the Gradle build.  Run them like this.

    gradle create-app
    gradle war

**To get a full list of commands, run this.**

    gradle tasks

Specify command line arguments either of these ways.  **If specifying arguments, you may only
execute one command** since all arguments will be passed to all commands.

    gradle create-app -Parg0=appName
    gradle create-app --project-prop arg0=appName