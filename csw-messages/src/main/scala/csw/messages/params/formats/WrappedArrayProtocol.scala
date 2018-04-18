package csw.messages.params.formats

import play.api.libs.json._

import scala.collection.mutable
import scala.reflect.ClassTag

/**
 * The serialization and deserialization format for scala.collection.immutable.WrappedArray.
 */
private[messages] trait WrappedArrayProtocol { self ⇒
  implicit def wrappedArrayFormat[T: Format: ClassTag]: Format[mutable.WrappedArray[T]] =
    new Format[mutable.WrappedArray[T]] {
      override def writes(obj: mutable.WrappedArray[T]): JsValue           = Json.toJson(obj.array)
      override def reads(json: JsValue): JsResult[mutable.WrappedArray[T]] = JsSuccess(json.as[Array[T]])
    }
}
