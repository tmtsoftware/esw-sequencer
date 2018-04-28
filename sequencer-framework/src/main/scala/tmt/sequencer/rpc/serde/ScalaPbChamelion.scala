package tmt.sequencer.rpc.serde

import java.nio.ByteBuffer

import chameleon._

import scala.util.{Failure, Success, Try}

object ScalaPbChamelion {
  implicit def pbChameleon[T](implicit tm: PbFormat[T]): SerializerDeserializer[T, ByteBuffer] =
    new Serializer[T, ByteBuffer] with Deserializer[T, ByteBuffer] {
      override def serialize(arg: T): ByteBuffer = tm.write(arg)
      override def deserialize(arg: ByteBuffer): Either[Throwable, T] = Try(tm.read(arg)) match {
        case Success(x) => Right(x)
        case Failure(t) => Left(t)
      }
    }
}
