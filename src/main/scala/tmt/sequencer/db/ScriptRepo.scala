package tmt.sequencer.db

import java.io.File

import ammonite.ops.Path
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand.ResetType
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.RefSpec

class ScriptRepo(scriptConfigs: ScriptConfigs) {
  //temporary vals will be replaced by location-service
  def gitHost = "0.0.0.0"
  def gitPort = 8080

  def gitRemote = s"http://$gitHost:$gitPort/${scriptConfigs.repoOwner}/${scriptConfigs.repoName}.git"

  private def cleanExistingRepo(file: File): Unit = {
    ammonite.ops.rm ! Path(file)
  }

  def cloneRepo(): Unit = {
    val cloneDir = new File(scriptConfigs.cloneDir)
    try {
      val refSpec = new RefSpec("+refs/*:refs/*")
      val repo    = gitRepo()

      repo.fetch().setRefSpecs(refSpec).call()

      repo.reset().setMode(ResetType.HARD).addPath(s"origin/${scriptConfigs.branch}").call()

      repo.clean().setForce(true).setCleanDirectories(true).call()

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

  private def gitRepo(): Git = new Git(
    new FileRepositoryBuilder()
      .setMustExist(true)
      .setGitDir(new File(s"${scriptConfigs.cloneDir}/.git"))
      .build()
  )
}
