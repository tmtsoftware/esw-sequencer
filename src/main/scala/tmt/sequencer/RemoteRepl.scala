package tmt.sequencer

import ammonite.sshd._
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator

class RemoteRepl(commandService: CswServices, engine: Engine) {

  def server() = new SshdRepl(
    SshServerConfig(
      address = "localhost", // or "0.0.0.0" for public-facing shells
      port = 22222, // Any available port
      passwordAuthenticator = Some(AcceptAllPasswordAuthenticator.INSTANCE) // or publicKeyAuthenticator
    ),
    predef = """
         |def setFlags() = repl.compiler.settings.Ydelambdafy.value = "inline"
      """.stripMargin,
    replArgs = Seq(
      "cs"      -> commandService,
      "engine"  -> engine,
      "Command" -> Command,
    )
  )
}
