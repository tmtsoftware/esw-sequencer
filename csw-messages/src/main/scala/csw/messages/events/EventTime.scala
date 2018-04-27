package csw.messages.events

import java.time.{Clock, Instant}

import com.google.protobuf.timestamp.Timestamp
import csw.messages.params.pb.Implicits.instantMapper
import play.api.libs.json._

import scalapb.TypeMapper

/**
 * A wrapper class representing the time of event creation
 *
 * @param time the instant stating the event creation
 */
case class EventTime(time: Instant) {
  override def toString: String = time.toString
}

object EventTime {

  //TODO: Have a correct implementation
  implicit val dd: Reads[Instant] = new Reads[Instant] {
    override def reads(json: JsValue): JsResult[Instant] = JsSuccess(Instant.now())
  }

  /**
   * The apply method is used to create EventTime using Instant.now in UTC timezone
   *
   * @return an EventTime representing event creation
   */
  //TODO: Use a clock
  def apply(): EventTime = new EventTime(Instant.now())

  private[messages] implicit val format: Format[EventTime] = new Format[EventTime] {
    def writes(et: EventTime): JsValue            = JsString(et.toString)
    def reads(json: JsValue): JsResult[EventTime] = JsSuccess(EventTime(json.as[Instant]))
  }

  //used by Protobuf for conversion between Timestamp <==> EventTime
  private[messages] implicit val typeMapper: TypeMapper[Timestamp, EventTime] =
    TypeMapper[Timestamp, EventTime] { x ⇒
      EventTime(instantMapper.toCustom(x))
    } { x ⇒
      instantMapper.toBase(x.time)
    }
}
