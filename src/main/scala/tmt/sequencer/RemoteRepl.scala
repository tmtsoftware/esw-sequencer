package tmt.sequencer

import ammonite.sshd._
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator

object RemoteRepl {
  def server(engine: Engine, commandService: CommandService) = new SshdRepl(
    SshServerConfig(
      address = "localhost", // or "0.0.0.0" for public-facing shells
      port = 22222, // Any available port
      passwordAuthenticator = Some(AcceptAllPasswordAuthenticator.INSTANCE) // or publicKeyAuthenticator
    ),
    predef =
      """
        |repl.frontEnd() = ammonite.repl.FrontEnd.JLineUnix
      """.stripMargin,
    replArgs = Seq(
      "E" -> engine,
      "CS" -> commandService
    )
  )
}
