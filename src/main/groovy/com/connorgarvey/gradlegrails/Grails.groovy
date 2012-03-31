package com.connorgarvey.gradlegrails

import org.gradle.api.Project

public class Grails {
  String version
  
  void configure(Project project) {
    if (version == null) {
      version = '2.0.2'
    }
  }
  
  void version(String version) {
    this.version = version
  }
}
