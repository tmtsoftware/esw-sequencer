package tmt.sequencer

import ammonite.sshd._
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator

object RemoteRepl {
  private def sshServerConfig = SshServerConfig(
    address = "localhost", // or "0.0.0.0" for public-facing shells
    port = 22222, // Any available port
    passwordAuthenticator = Some(AcceptAllPasswordAuthenticator.INSTANCE) // or publicKeyAuthenticator
  )

  private def initialCommands: String =
    """import tmt.services.Command
       import tmt.sequencer.Dsl._
       repl.compiler.settings.Ydelambdafy.value = "inline"
      """.stripMargin

  def server = new SshdRepl(sshServerConfig, initialCommands)
}
