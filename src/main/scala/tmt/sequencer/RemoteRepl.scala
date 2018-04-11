package tmt.sequencer

import ammonite.sshd._
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator
import tmt.sequencer.dsl.CswServices
import tmt.sequencer.models.{Command, Id}
import tmt.sequencer.rpc.api.{SequenceManager, SequenceProcessor}
import tmt.sequencer.rpc.server.RpcConfigs

class RemoteRepl(commandService: CswServices,
                 sequencer: Sequencer,
                 sequenceProcessor: SequenceProcessor,
                 sequenceManager: SequenceManager,
                 rpcConfigs: RpcConfigs) {

  def server() = new SshdRepl(
    SshServerConfig(
      address = "0.0.0.0",
      port = rpcConfigs.port + 100,
      passwordAuthenticator = Some(AcceptAllPasswordAuthenticator.INSTANCE) // or publicKeyAuthenticator
    ),
    predef = """
         |def setFlags() = repl.compiler.settings.Ydelambdafy.value = "inline"
         |import scala.concurrent.duration.Duration
         |import scala.concurrent.{Await, Future}
         |implicit class RichFuture[T](val f: Future[T]) {
         |  def get: T = Await.result(f, Duration.Inf)
         |}
      """.stripMargin,
    replArgs = Seq(
      "cs"                -> commandService,
      "sequencer"         -> sequencer,
      "sequenceProcessor" -> sequenceProcessor,
      "sequenceManager"   -> sequenceManager,
      "Command"           -> Command,
      "Id"                -> Id,
    )
  )
}
