package tmt.sequencer.scripts

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

class ScriptConfigs(sequencerId: String, oMode: String)(implicit actorSystem: ActorSystem) {

  private lazy val config: Config = ConfigFactory
    .parseResources(s"$sequencerId.conf")
    .withFallback(actorSystem.settings.config)

  lazy val scriptClass: String =
    Try(
      config.getString(s"scripts.$sequencerId.$oMode.scriptClass")
    ).toOption
      .getOrElse(
        throw new RuntimeException(s"Please provide script class for $sequencerId in configuration settings")
      )
}
