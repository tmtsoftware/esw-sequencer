package tmt.sequencer.config

import akka.actor.ActorSystem
import com.typesafe.config.Config

class ScriptConfigs(actorSystem: ActorSystem) {

  private val config: Config = actorSystem.settings.config.getConfig("scripts")

  val scriptFactoryCanonicalPath: String = config.getString("canonical-path")
}
