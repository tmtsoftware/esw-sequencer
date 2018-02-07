package tmt.sequencer

import ammonite.sshd._
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator

object RemoteRepl {
  lazy val server = new SshdRepl(
    SshServerConfig(
      address = "localhost", // or "0.0.0.0" for public-facing shells
      port = 22222, // Any available port
      passwordAuthenticator = Some(AcceptAllPasswordAuthenticator.INSTANCE) // or publicKeyAuthenticator
    ),
    predef =
      """
        |import tmt.services.Command
        |import tmt.sequencer.Dsl._
        |repl.compiler.settings.Ydelambdafy.value = "inline"
      """.stripMargin
  )
}
