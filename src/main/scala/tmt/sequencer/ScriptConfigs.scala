package tmt.sequencer

import akka.actor.typed.ActorSystem
import com.typesafe.config.Config

class ScriptConfigs(actorSystem: ActorSystem[_]) {

  private val config: Config = actorSystem.settings.config.getConfig("scripts")

  val cloneDir: String  = config.getString("clone-dir")
  val repoOwner: String = config.getString("repo.owner")
  val repoName: String  = config.getString("repo.name")
  val branch: String    = config.getString("repo.branch")
}
