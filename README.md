Gradle Grails Wrapper
=====================

This is a plugin for [Gradle](http://www.gradle.org) that builds [Grails](http://www.grails.org) applications.  It allows you to use Gradle as a build tool and allows you to use multiple versions of Grails on one machine without specifying full `grails` paths.

Applying the plugin
-------------------

    buildscript {
      repositories {
        mavenCentral()
        maven { url 'http://connorwgarvey.github.com/gradle-grails-wrapper/repo/' }
      }

      dependencies {
        classpath 'com.connorgarvey.gradle:gradle-grails-wrapper:0.2'
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

How it works
------------

When executed, the plugin downloads a copy of Grails to a `.gradlegrails` folder in the user's home directory.  It can maintain multiple versions of Grails on the same machine.  It executes the specified command on the downloaded copy.
