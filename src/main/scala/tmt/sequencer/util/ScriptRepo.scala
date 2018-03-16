package tmt.sequencer.util

import java.io.File

import org.eclipse.jgit.api.Git

object ScriptRepo {
  def getFile(basePath: String) = new File(basePath)

  private def cleanExistingRepo(file: File): Unit = {
    if (file.isDirectory)
      file.listFiles.foreach(cleanExistingRepo)
    if (file.exists && !file.delete)
      throw new Exception(s"Unable to delete ${file.getAbsolutePath}")
  }

  def clone(remote: String, basePath: String): Git = {
    val file = getFile(basePath)

    cleanExistingRepo(file)

    Git
      .cloneRepository()
      .setURI(remote)
      .setDirectory(file)
      .setBranch("refs/heads/master")
      .call();
  }

}
