package tmt.sequencer.scripts

import akka.actor.ActorSystem
import com.typesafe.config.Config

import scala.util.Try

class ScriptConfigs(sClass: Option[String], oMode: Option[String])(implicit actorSystem: ActorSystem) {

  private val config: Config     = actorSystem.settings.config.getConfig("scripts")
  private val defaultSequencerId = config.getString("defaultSequencerId")

  val observingMode: String = oMode.getOrElse(config.getString("defaultObservingMode"))

  lazy val scriptClass: String =
    Try(
      sClass.getOrElse(
        config
          .getConfig(s"$defaultSequencerId.$observingMode")
          .getString("scriptClass")
      )
    ).toOption
      .getOrElse(
        throw new RuntimeException("Please provide script class either through command line or configuration settings")
      )
}
