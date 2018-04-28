package tmt.sequencer.rpc.serde

import java.nio.ByteBuffer

import chameleon._
import io.scalaland.chimney.Transformer
import io.scalaland.chimney.dsl._
import scalapb.TypeMapper
import sequencer_protobuf.command.PbCommandResponse.CommandResponse.Empty
import sequencer_protobuf.command._
import tmt.sequencer.models.CommandResponse.{Failed, Success}
import tmt.sequencer.models._

import scala.util.{Failure, Try, Success => Succ}

object TypeMappings {
  implicit def commandListT[T]: Transformer[List[Command], PbCommandList] = xs => PbCommandList(xs.transformInto[List[PbCommand]])
  implicit def commandList2[T]: Transformer[PbCommandList, List[Command]] = x => x.commands.toList.transformInto[List[Command]]

  implicit val crT: Transformer[PbCommandResponse, CommandResponse] = _.commandResponse match {
    case Empty                                            => Empty.value
    case PbCommandResponse.CommandResponse.Success(value) => value.transformInto[Success]
    case PbCommandResponse.CommandResponse.Failed(value)  => value.transformInto[Failed]
  }

  implicit val crT2: Transformer[CommandResponse, PbCommandResponse] = {
    case x: Success => PbCommandResponse(PbCommandResponse.CommandResponse.Success(x.transformInto[PbSuccess]))
    case x: Failed  => PbCommandResponse(PbCommandResponse.CommandResponse.Failed(x.transformInto[PbFailed]))
  }

  implicit def list2Set[A, B](implicit t: Transformer[A, B]): Transformer[Seq[A], Set[B]] = _.toSet[A].transformInto[Set[B]]

  implicit val reponseT: Transformer[AggregateResponse, PbAggregateResponse]  = _.transformInto[PbAggregateResponse]
  implicit val reponseT2: Transformer[PbAggregateResponse, AggregateResponse] = _.transformInto[AggregateResponse]

  /////////////////
  implicit def typeMapper[A, B](implicit a2B: Transformer[A, B], b2a: Transformer[B, A]): TypeMapper[A, B] =
    TypeMapper[A, B](a2B.transform)(b2a.transform)

  implicit val commandListTm: TypeMapper[PbCommandList, List[Command]] = typeMapper

  implicit val aggregateResponseTm: TypeMapper[PbAggregateResponse, AggregateResponse] = typeMapper

  ///////////////////
  implicit val commandsFormat: PbFormat[List[Command]]    = PbFormat.of[List[Command], PbCommandList]
  implicit val resposeFormat: PbFormat[AggregateResponse] = PbFormat.of[AggregateResponse, PbAggregateResponse]

  implicit def pbChameleon[T](implicit tm: PbFormat[T]): SerializerDeserializer[T, ByteBuffer] =
    new Serializer[T, ByteBuffer] with Deserializer[T, ByteBuffer] {
      override def serialize(arg: T): ByteBuffer = tm.write(arg)
      override def deserialize(arg: ByteBuffer): Either[Throwable, T] = Try(tm.read(arg)) match {
        case Succ(x)    => Right(x)
        case Failure(t) => Left(t)
      }
    }

//  implicit val commandListSerde: SerializerDeserializer[List[Command], ByteBuffer]  = pbChameleon[List[Command]]
//  implicit val responseSerde: SerializerDeserializer[AggregateResponse, ByteBuffer] = pbChameleon[AggregateResponse]
}
