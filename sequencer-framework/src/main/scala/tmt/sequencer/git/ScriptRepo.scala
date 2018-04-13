package tmt.sequencer.git

import java.io.File

import ammonite.ops.Path
import org.eclipse.jgit.api.Git
import org.eclipse.jgit.api.ResetCommand.ResetType
import org.eclipse.jgit.storage.file.FileRepositoryBuilder
import org.eclipse.jgit.transport.RefSpec
import tmt.sequencer.gateway.LocationService

class ScriptRepo(scriptConfigs: ScriptConfigs, locationService: LocationService) {
  private val (host, port) = locationService.gitAddress()
  private val gitRemote    = s"http://$host:$port/${scriptConfigs.repoOwner}/${scriptConfigs.repoName}.git"

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
