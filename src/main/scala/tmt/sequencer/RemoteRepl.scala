package tmt.sequencer

import ammonite.sshd._
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator

object RemoteRepl {
  val server = new SshdRepl(
    SshServerConfig(
      address = "localhost", // or "0.0.0.0" for public-facing shells
      port = 22222, // Any available port
      passwordAuthenticator = Some(AcceptAllPasswordAuthenticator.INSTANCE) // or publicKeyAuthenticator
    ),
    predef =
      """
        |repl.frontEnd() = ammonite.repl.FrontEnd.JLineUnix
        |println("Starting Debugging!")
        |import tmt.sequencer.Dsl
        |val dsl: Dsl = Dsl.build()
        |import dsl._
        |import tmt.sequencer.Wiring.engine
        |import tmt.sequencer.Engine.Push
      """.stripMargin
  )
}
