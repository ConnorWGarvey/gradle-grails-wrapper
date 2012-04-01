Gradle Grails Wrapper
=====================

This is a plugin for [Gradle](http://www.gradle.org) that builds [Grails](http://www.grails.org) applications.  It differs from gradle-grails-plugin in that it is much simpler so that, hopefully, it will be more functional.

Using the plugin
----------------

    buildscript {
      repositories {
        mavenCentral()
        maven { url 'http://connorwgarvey.github.com/gradle-grails-wrapper/repo/' }
      }

      dependencies {
        classpath 'com.connorgarvey.gradle:gradle-grails-wrapper:0.1-SNAPSHOT'
      }
    }
    
    apply plugin: 'grails'

By default, the plugin will select a version of Grails.  To specify a version, add this to the build.

    grails {
      version '2.0.2' 
    }
