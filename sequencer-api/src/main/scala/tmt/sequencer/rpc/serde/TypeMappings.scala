package tmt.sequencer.rpc.serde

import java.nio.ByteBuffer

import chameleon.{SerializerDeserializer, _}
import com.google.protobuf.ByteString
import com.google.protobuf.wrappers.BytesValue
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import scalapb.TypeMapper
import sequencer_protobuf.command.PbCommandResponse.CommandResponse.Empty
import sequencer_protobuf.command._
import tmt.sequencer.models.CommandResponse.{Failed, Success}
import tmt.sequencer.models._

import scala.util.{Failure, Try, Success => Succ}

object TypeMappings {

  implicit def dd1[A, B](implicit t: Transformer[A, B]): Transformer[Seq[A], Set[B]] = _.toSet[A].transformInto[Set[B]]
  implicit def dd2[A, B](implicit t: Transformer[A, B]): Transformer[Set[A], Seq[B]] = _.toSeq.transformInto[Seq[B]]

  implicit val crT: Transformer[PbCommandResponse, CommandResponse] = _.commandResponse match {
    case Empty                                            => Empty.value
    case PbCommandResponse.CommandResponse.Success(value) => value.transformInto[Success]
    case PbCommandResponse.CommandResponse.Failed(value)  => value.transformInto[Failed]
  }

  implicit val crT2: Transformer[CommandResponse, PbCommandResponse] = {
    case x: Success => PbCommandResponse(PbCommandResponse.CommandResponse.Success(x.transformInto[PbSuccess]))
    case x: Failed  => PbCommandResponse(PbCommandResponse.CommandResponse.Failed(x.transformInto[PbFailed]))
  }

  implicit val commandListTm: TypeMapper[PbCommandList, CommandList] =
    TypeMapper[PbCommandList, CommandList](_.transformInto[CommandList])(_.transformInto[PbCommandList])

  implicit val aggregateResponseTm: TypeMapper[PbAggregateResponse, AggregateResponse] =
    TypeMapper[PbAggregateResponse, AggregateResponse](_.transformInto[AggregateResponse])(_.transformInto[PbAggregateResponse])

  implicit val tt: TypeMapper[BytesValue, ByteBuffer] =
    TypeMapper[BytesValue, ByteBuffer](
      x => ByteBuffer.wrap(x.toByteString.toByteArray)
    )(
      x => BytesValue().withValue(ByteString.copyFrom(x.array()))
    )
  ///////////////////
  implicit val commandsFormat: PbFormat[CommandList]      = PbFormat.of[CommandList, PbCommandList]
  implicit val resposeFormat: PbFormat[AggregateResponse] = PbFormat.of[AggregateResponse, PbAggregateResponse]
  implicit val ee1: PbFormat[String]                      = PbFormat.of[String, com.google.protobuf.wrappers.StringValue]
  implicit val ee3: PbFormat[Int]                         = PbFormat.of[Int, com.google.protobuf.wrappers.Int32Value]
  implicit val ee4: PbFormat[Double]                      = PbFormat.of[Double, com.google.protobuf.wrappers.DoubleValue]
  implicit val ee5: PbFormat[Float]                       = PbFormat.of[Float, com.google.protobuf.wrappers.FloatValue]
  implicit val ee6: PbFormat[Boolean]                     = PbFormat.of[Boolean, com.google.protobuf.wrappers.BoolValue]
  implicit val ee7: PbFormat[ByteBuffer]                  = PbFormat.of[ByteBuffer, com.google.protobuf.wrappers.BytesValue]

  implicit def pbChameleon[T](implicit tm: PbFormat[T]): SerializerDeserializer[T, ByteBuffer] =
    new Serializer[T, ByteBuffer] with Deserializer[T, ByteBuffer] {
      override def serialize(arg: T): ByteBuffer = tm.write(arg)
      override def deserialize(arg: ByteBuffer): Either[Throwable, T] = Try(tm.read(arg)) match {
        case Succ(x)    => Right(x)
        case Failure(t) => Left(t)
      }
    }

//  implicit val commandListSerde: SerializerDeserializer[CommandList, ByteBuffer]    = pbChameleon[CommandList]
//  implicit val responseSerde: SerializerDeserializer[AggregateResponse, ByteBuffer] = pbChameleon[AggregateResponse]
}

object DD {

  import TypeMappings._

  def main(args: Array[String]): Unit = {
    val commandList = CommandList.from(Command(Id("A"), "setup-iris", List()), Command(Id("B"), "setup-iris", List()))

    println(Command(Id("A"), "setup-iris", List()).transformInto[PbCommand].transformInto[Command])
    println(commandList.transformInto[PbCommandList].transformInto[CommandList])

    println(Success(Id("123"), "asdasd").asInstanceOf[CommandResponse].transformInto[PbCommandResponse])
    val response = AggregateResponse.add(Success(Id("123"), "asdasd"))
    println(
      response.transformInto[PbAggregateResponse].transformInto[AggregateResponse]
    )

    val value = implicitly[SerializerDeserializer[ByteBuffer, ByteBuffer]]
  }
}
