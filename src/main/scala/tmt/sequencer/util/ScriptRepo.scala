package tmt.sequencer.util

import java.io.File

import org.eclipse.jgit.api.Git
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
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
    try {
      git().pull().call()
    } catch {
      case ex: Exception =>
        cleanExistingRepo(cloneDir)
        Git
          .cloneRepository()
          .setURI(gitRemote)
          .setDirectory(cloneDir)
          .setBranch("refs/heads/master")
          .call()
    }
  }

  private def git(): Git = new Git(
    new FileRepositoryBuilder()
      .setMustExist(true)
      .setGitDir(new File(s"${scriptConfigs.cloneDir}/.git"))
      .build()
  )
}
