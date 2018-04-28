package tmt.sequencer.rpc.serde

import com.google.protobuf.ByteString
import scalapb.{GeneratedMessage, GeneratedMessageCompanion, Message, TypeMapper}
import java.nio.ByteBuffer

trait PbFormat[T] {
  def write(x: T): ByteBuffer
  def read(x: ByteBuffer): T
}

object PbFormat {
  def of[T, PbType <: GeneratedMessage with Message[PbType]](implicit typeMapper: TypeMapper[PbType, T],
                                                             PbType: GeneratedMessageCompanion[PbType]): PbFormat[T] =
    new PbFormat[T] {
      override def write(x: T): ByteBuffer = typeMapper.toBase(x).toByteString.asReadOnlyByteBuffer()
      override def read(x: ByteBuffer): T  = typeMapper.toCustom(PbType.parseFrom(ByteString.copyFrom(x).toByteArray))
    }
}
