package tmt.sequencer.db

import akka.actor.ActorSystem
import com.typesafe.config.Config

class RpcConfigs(actorSystem: ActorSystem) {
  private val config: Config = actorSystem.settings.config.getConfig("rpc.server")

  val port: Int = config.getInt("port")
}
