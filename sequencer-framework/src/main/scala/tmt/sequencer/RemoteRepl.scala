package tmt.sequencer

import ammonite.sshd._
import org.apache.sshd.server.auth.password.AcceptAllPasswordAuthenticator
import tmt.sequencer.api.{SequenceEditor, SequenceFeeder}
import tmt.sequencer.dsl.CswServices
import tmt.sequencer.models.{Command, Id}
import tmt.sequencer.rpc.server.RpcConfigs

class RemoteRepl(commandService: CswServices,
                 sequencer: Sequencer,
                 sequenceFeeder: SequenceFeeder,
                 sequenceEditor: SequenceEditor,
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
      "cs"             -> commandService,
      "sequencer"      -> sequencer,
      "sequenceFeeder" -> sequenceFeeder,
      "sequenceEditor" -> sequenceEditor,
      "Command"        -> Command,
      "Id"             -> Id,
    )
  )
}