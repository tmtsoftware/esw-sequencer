package csw.messages.params

import java.util.Optional

import akka.actor
import akka.actor.typed.scaladsl.adapter.UntypedActorSystemOps
import akka.actor.typed.{ActorRef, ActorSystem}
import akka.testkit.typed.TestKitSettings
import akka.testkit.typed.scaladsl.TestProbe
import akka.util.Timeout
import csw.messages.commands.{Command, CommandName, Setup}
import csw.messages.events.SystemEvent
import csw.messages.params.generics.{KeyType, Parameter}
import csw.messages.params.models.{ObsId, Prefix}
import org.scalatest.{BeforeAndAfterAll, FunSuite, Matchers}

import scala.collection.JavaConverters.collectionAsScalaIterableConverter
import scala.concurrent.duration.DurationInt
import scala.concurrent.{Await, Future}

case class CommandMsg(
    command: Command,
    ackTo: ActorRef[java.util.Set[Parameter[_]]],
    replyTo: ActorRef[SystemEvent],
    obsIdAck: ActorRef[Optional[ObsId]]
)

// DEOPSCSW-184: Change configurations - attributes and values
class InterOperabilityTest extends FunSuite with Matchers with BeforeAndAfterAll {
  implicit val timeout: Timeout = Timeout(5.seconds)

  private val prefixStr    = "wfos.red.detector"
  private val obsId: ObsId = ObsId("Obs001")
  private val intKey       = KeyType.IntKey.make("intKey")
  private val stringKey    = KeyType.StringKey.make("stringKey")
  private val intParam     = intKey.set(22, 33)
  private val stringParam  = stringKey.set("First", "Second")

  private val system: actor.ActorSystem          = actor.ActorSystem("test")
  implicit val typedSystem: ActorSystem[Nothing] = system.toTyped
  implicit val testKit: TestKitSettings          = TestKitSettings(typedSystem)

  private val scalaSetup = Setup(Prefix(prefixStr), CommandName(prefixStr), Some(obsId)).add(intParam).add(stringParam)

  private val javaCmdHandlerBehavior: Future[ActorRef[CommandMsg]] =
    typedSystem.systemActorOf[CommandMsg](JavaCommandHandler.behavior(), "javaCommandHandler")

  private val jCommandHandlerActor: ActorRef[CommandMsg] = Await.result(javaCmdHandlerBehavior, 5.seconds)

  override protected def afterAll(): Unit = Await.result(system.terminate(), 5.seconds)

  // 1. sends scala Setup command to Java Actor
  // 2. onMessage, Java actor extracts paramSet from Setup command and replies back to scala actor
  // 3. also, java actor creates StatusEvent and forward it to scala actor
  test("should able to send commands/events from scala code to java and vice a versa") {
    val ackProbe     = TestProbe[java.util.Set[Parameter[_]]]
    val replyToProbe = TestProbe[SystemEvent]
    val obsIdProbe   = TestProbe[Optional[ObsId]]

    jCommandHandlerActor ! CommandMsg(scalaSetup, ackProbe.ref, replyToProbe.ref, obsIdProbe.ref)

    val set = ackProbe.expectMessageType[java.util.Set[Parameter[_]]]
    set.asScala.toSet shouldBe Set(intParam, stringParam)

    val eventFromJava = replyToProbe.expectMessageType[SystemEvent]
    eventFromJava.paramSet shouldBe Set(JavaCommandHandler.encoderParam, JavaCommandHandler.epochStringParam)

    obsIdProbe.expectMessageType[Optional[ObsId]]
  }

}
