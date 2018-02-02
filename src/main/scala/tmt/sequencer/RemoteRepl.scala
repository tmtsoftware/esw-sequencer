package tmt.sequencer

import akka.typed.ActorRef
import ammonite.sshd._
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator

object RemoteRepl {
  def server(engine: ActorRef[Engine.Command], dsl: Dsl) = new SshdRepl(
    SshServerConfig(
      address = "localhost", // or "0.0.0.0" for public-facing shells
      port = 22222, // Any available port
      passwordAuthenticator = Some(AcceptAllPasswordAuthenticator.INSTANCE) // or publicKeyAuthenticator
    ),
    predef =
      """
        |repl.frontEnd() = ammonite.repl.FrontEnd.JLineUnix
        |import dsl._
        |import tmt.sequencer.Engine.Push
      """.stripMargin,
    replArgs = Seq(
      "engine" -> engine,
      "dsl" -> dsl
    )
  )
}
