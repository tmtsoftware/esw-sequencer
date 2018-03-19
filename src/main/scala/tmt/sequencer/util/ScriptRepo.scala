package tmt.sequencer.util

import java.io.File

import ammonite.ops.Path
import org.eclipse.jgit.api.Git
import tmt.sequencer.ScriptConfigs

class ScriptRepo(scriptConfigs: ScriptConfigs) {
  //temporary vals will be replaced by location-service
  def gitHost = "0.0.0.0"
  def gitPort = 8080

  def gitRemote = s"http://$gitHost:$gitPort/${scriptConfigs.repoOwner}/${scriptConfigs.repoName}.git"

  private def cleanExistingRepo(file: File): Unit = {
    if (file.isDirectory)
      file.listFiles.foreach(cleanExistingRepo)
    if (file.exists && !file.delete)
      throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
  }

  def cloneRepo(): Unit = {
    val cloneDir = new File(scriptConfigs.cloneDir)

    cleanExistingRepo(cloneDir)

    Git
      .cloneRepository()
      .setURI(gitRemote)
      .setDirectory(cloneDir)
      .setBranch("refs/heads/master")
      .call()
  }
}
