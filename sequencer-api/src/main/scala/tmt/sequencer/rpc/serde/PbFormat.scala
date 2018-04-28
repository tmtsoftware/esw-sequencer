package tmt.sequencer.rpc.serde

import java.nio.ByteBuffer

import scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message, TypeMapper}

trait PbFormat[T] {
  def write(x: T): ByteBuffer
  def read(x: ByteBuffer): T
}

object PbFormat {
  def of[T, PbType <: GeneratedMessage with Message[PbType]](implicit typeMapper: TypeMapper[PbType, T],
                                                             PbType: GeneratedMessageCompanion[PbType]): PbFormat[T] =
    new PbFormat[T] {
      override def write(x: T): ByteBuffer = ByteBuffer.wrap(typeMapper.toBase(x).toByteArray)
      override def read(x: ByteBuffer): T  = typeMapper.toCustom(PbType.parseFrom(x.array()))
    }
}
