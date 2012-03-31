package com.connorgarvey.gradlegrails

import org.apache.commons.lang3.SystemUtils
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemManager
import org.apache.commons.vfs2.Selectors
import org.apache.commons.vfs2.VFS
import org.gradle.api.Plugin
import org.gradle.api.Project

/**
 * @author Connor Garvey
 * @since March 31, 2012
 */
class GrailsPlugin implements Plugin<Project> {
  
  private void addTask(Project project, String target) {
    project.task(target) << {
      project.grails.configure()
      build(project, target)
    }
  }
  
  @Override
  public void apply(Project project) {
    configure(project)
    addTask(project, 'war')
    addTask(project, 'create-app')
  }
  
  private void build(Project project, String target) {
    String grailsFolder = download(project.grails.version)
    String extension = SystemUtils.IS_OS_WINDOWS ? '.bat' : ''
    Process process = "${Path.join(grailsFolder, 'bin', 'grails' + extension)} -plain-output ${target}".execute()
    String s = null
    StringBuilder output = new StringBuilder()
    BufferedReader input = new BufferedReader(new InputStreamReader(process.inputStream))
    while ((s = input.readLine()) != null) {
      output.append(s + '\n')
    }
    BufferedReader error = new BufferedReader(new InputStreamReader(process.errorStream))
    while ((s = error.readLine()) != null) {
      output.append(s + '\n')
    }
    process.waitFor()
    if (process.exitValue() != 0) {
      println output.toString()
      throw new IllegalStateException("Grails ${target} failed. See above for output.")
    }
  }
  
  private void configure(Project project) {
    project.extensions.create('grails', Grails)
  }
  
  private String download(String version) {
    FileSystemManager manager = VFS.manager
    String homePath = Path.join(SystemUtils.userHome.path, '.gradlegrails')
    FileObject destination = manager.resolveFile("file://" + Path.join(homePath, 'grails'))
    String grailsPath = Path.join(destination.name.path, "grails-${version}")
    if (new File(grailsPath).exists()) {
      return grailsPath
    }
    String url =
        "http://dist.springframework.org.s3.amazonaws.com/release/GRAILS/grails-${version}.zip"
    File zipFile = new File(Path.join(homePath, 'archive', "${version}.zip"))
    if (!zipFile.parentFile.exists() && !zipFile.parentFile.mkdirs()) {
      throw new IllegalStateException("Could not create ${zipFile.path}")
    }
    OutputStream file = new FileOutputStream(zipFile)
    OutputStream out = new BufferedOutputStream(file)
    out << new URL(url).openStream()
    out.close()
    FileObject zip = manager.resolveFile("zip:file://${zipFile.path}")
    destination.copyFrom(zip, Selectors.SELECT_ALL)
    makeGrailsExecutable(grailsPath)
    return grailsPath
  }
  
  private void makeGrailsExecutable(String path) {
    if (SystemUtils.IS_OS_WINDOWS) {
      return
    }
    File file = new File(Path.join(path, 'bin', 'grails'))
    file.executable = true
  }
}
