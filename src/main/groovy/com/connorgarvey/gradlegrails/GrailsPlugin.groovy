package com.connorgarvey.gradlegrails

import org.apache.commons.lang3.SystemUtils
import org.apache.commons.vfs2.FileObject
import org.apache.commons.vfs2.FileSystemManager
import org.apache.commons.vfs2.Selectors
import org.apache.commons.vfs2.VFS
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.api.Task
import org.gradle.api.tasks.Exec

/**
 * @author Connor Garvey
 * @since March 31, 2012
 */
class GrailsPlugin implements Plugin<Project> {
  
  private void addTask(Project project, String target) {
    Task task = project.task("grails-${target}") << {
      project.grails.configure()
      String grailsFolder = makeGrailsPath(project.grails.version)
      String extension = SystemUtils.IS_OS_WINDOWS ? '.bat' : ''
      List<String> args = collectArguments(project)
      List<String> systemProperties = collectSystemProperties(project)
      List<String> command = [
        Path.join(grailsFolder, 'bin', "grails${extension}"),
        '-plain-output'
      ]
      command.addAll(systemProperties.collect { "-D${it}" })
      command.add(target)
      command.addAll(args)
      Map<String, String> env = new HashMap(System.getenv())
      env['GRAILS_HOME'] = grailsFolder
      project.exec {
        commandLine command
        environment env
        standardInput = System.in
      }
    }
    task.configure {
      group 'Grails'
      description "Run Grails ${target}"
    }
    task.dependsOn('install-grails')
  }
  
  private List<String> collectArguments(Project project) {
    collectProperties(project, 'arg')
  }
  
  private List<String> collectProperties(Project project, String prefix) {
    List<String> result = []
    int index = 0
    try {
      while (true) {
        result << project."${prefix}${index++}"
      }
    }
    catch (MissingPropertyException ex) {
    }
    return result
  }
  
  private List<String> collectSystemProperties(Project project) {
    collectProperties(project, 'd')
  }
  
  @Override
  public void apply(Project project) {
    configure(project)
    project.task('install-grails') << {
      project.grails.configure()
      download(project.grails.version)
    }
    [
      'add-proxy',
      'bootstrap',
      'bug-report',
      'clean',
      'clear-proxy',
      'compile',
      'console',
      'create-app',
      'create-controller',
      'create-domain-class',
      'create-filters',
      'create-hibernate-cfg-xml',
      'create-integration-test',
      'create-plugin',
      'create-scaffold-controller',
      'create-script',
      'create-service',
      'create-tag-lib',
      'create-unit-test',
      'dependency-report',
      'doc',
      'generate-all',
      'generate-controller',
      'generate-views',
      'help',
      'init',
      'install-dependency',
      'install-plugin',
      'install-templates',
      'integrate-with',
      'interactive',
      'list-plugin-updates',
      'list-plugins',
      'migrate-docs',
      'package',
      'package-plugin',
      'plugin-info',
      'refresh-dependencies',
      'remove-proxy',
      'run-app',
      'run-script',
      'run-war',
      'schema-export',
      'set-proxy',
      'set-version',
      'shell',
      'stats',
      'test-app',
      'uninstall-plugin',
      'upgrade',
      'war',
    ].each { addTask(project, it) }
  }
  
  private void build(Project project, String target) {
    String grailsFolder = makeGrailsPath(project.grails.version)
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
    String destinationPath = Path.join(homePath, 'grails', version)
    FileObject destination = manager.resolveFile(destinationPath)
    if (destination.exists()) {
      return destination.name.path
    }
    File zipFile = downloadZip(homePath, version)
    FileObject zip = manager.resolveFile("zip:file://${zipFile.path}")
    unzipGrails(homePath, zip, destinationPath)
    makeGrailsExecutable(destination.name.path)
    return destination.name.path
  }
  
  private File downloadZip(String homePath, String version) {
    println "Downloading Grails ${version}"
    File zipFile = new File(Path.join(homePath, 'archive', "${version}.zip"))
    if (!zipFile.parentFile.exists() && !zipFile.parentFile.mkdirs()) {
      throw new IllegalStateException("Could not create ${zipFile.path}")
    }
    OutputStream file = new FileOutputStream(zipFile)
    OutputStream out = new BufferedOutputStream(file)
    out << new URL(
        "http://dist.springframework.org.s3.amazonaws.com/release/GRAILS/grails-${version}.zip").
        openStream()
    out.close()
    return zipFile
  }
  
  private void makeGrailsExecutable(String path) {
    if (SystemUtils.IS_OS_WINDOWS) {
      return
    }
    File file = new File(Path.join(path, 'bin', 'grails'))
    file.executable = true
  }
  
  private String makeGrailsPath(String version) {
    Path.join(SystemUtils.userHome.path, '.gradlegrails', 'grails', version)
  }
  
  private void unzipGrails(String homePath, FileObject zip, String destinationPath) {
    FileSystemManager manager = VFS.manager
    FileObject temporary = manager.resolveFile(Path.join(homePath, 'tmp'))
    temporary.copyFrom(zip, Selectors.SELECT_ALL)
    FileObject grailsHome = temporary
    while (!grailsHome.children.any { it.name.baseName == 'bin' }) {
      grailsHome = grailsHome.children[0]
    }
    for (child in grailsHome.children) {
      manager.resolveFile(Path.join(destinationPath, child.name.baseName)).copyFrom(child, Selectors.SELECT_ALL)
    }
    temporary.delete(Selectors.SELECT_ALL)
  }
}
