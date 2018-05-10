package tmt.sequencer.rpc.client

import akka.actor.ActorSystem
import akka.http.scaladsl.unmarshalling.FromByteStringUnmarshaller
import akka.stream.{ActorMaterializer, Materializer}
import akka.util.ByteString
import covenant.http.HttpClient
import sloth.{Client, ClientException}
import io.circe.generic.auto._
import chameleon.ext.circe._
import tmt.sequencer.api.{SequenceEditor, SequenceFeeder}

import scala.concurrent.{ExecutionContext, Future}

class JvmSequencerClient(baseUri: String)(implicit system: ActorSystem) {
  private implicit val materializer: ActorMaterializer = ActorMaterializer()

  implicit val StringUnmarshaller: FromByteStringUnmarshaller[String] = new FromByteStringUnmarshaller[String] {
    def apply(value: ByteString)(implicit ec: ExecutionContext, materializer: Materializer): Future[String] =
      Future.successful(value.utf8String)
  }

  private val client: Client[String, Future, ClientException] = HttpClient[String](baseUri)

  val sequenceFeeder: SequenceFeeder = client.wire[SequenceFeeder]
  val sequenceEditor: SequenceEditor = client.wire[SequenceEditor]
}
