package tmt.sequencer.scripts

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

import scala.util.Try

class ScriptConfigs(sequencerId: String, observingMode: String)(implicit actorSystem: ActorSystem) {

  private lazy val config: Config = actorSystem.settings.config

  lazy val scriptClass: String =
    Try(
      config.getString(s"scripts.$sequencerId.$observingMode.scriptClass")
    ).toOption
      .getOrElse(
        throw new RuntimeException(s"Please provide script class for $sequencerId in configuration settings")
      )
}
