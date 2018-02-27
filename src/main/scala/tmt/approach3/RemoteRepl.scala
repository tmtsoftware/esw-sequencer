package tmt.approach3

import ammonite.sshd._
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator
import tmt.sequencer.Wiring

object RemoteRepl {
  def server(wiring: Wiring) = new SshdRepl(
    SshServerConfig(
      address = "localhost", // or "0.0.0.0" for public-facing shells
      port = 22222, // Any available port
      passwordAuthenticator = Some(AcceptAllPasswordAuthenticator.INSTANCE) // or publicKeyAuthenticator
    ),
    predef = """
         |def setFlags() = repl.compiler.settings.Ydelambdafy.value = "inline"
      """.stripMargin,
    replArgs = Seq(
      "cs"     -> wiring.commandService,
      "engine" -> wiring.engine,
    )
  )
}
