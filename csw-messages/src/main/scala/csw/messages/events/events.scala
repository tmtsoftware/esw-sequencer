package csw.messages.events

import java.time.Instant

import csw.messages.params.generics.{Parameter, ParameterSetType}
import csw.messages.params.models.{Id, Prefix}
import csw_protobuf.events.PbEvent
import csw_protobuf.events.PbEvent.PbEventType
import csw_protobuf.parameter.PbParameter

import scalapb.TypeMapper

/**
 * Common trait representing events in TMT like SystemEvent and ObserveEvent
 */
sealed trait Event { self: ParameterSetType[_] ⇒

  /**
   * A helper to give access of public members of ParameterSetType
   *
   * @return a handle to ParameterSetType extended by concrete implementation of this class
   */
  def paramType: ParameterSetType[_] = self

  /**
   * unique Id for event
   */
  val eventId: Id

  /**
   * Prefix representing source of the event
   */
  val source: Prefix

  /**
   * The name of event
   */
  val eventName: EventName

  /**
   * The time of event creation
   */
  val eventTime: EventTime

  /**
   * An optional initial set of parameters (keys with values)
   */
  val paramSet: Set[Parameter[_]]

  /**
   * A name identifying the type of parameter set, such as "SystemEvent", "ObserveEvent".
   * This is used in the JSON and toString output.
   *
   * @return a string representation of concrete type of this class
   */
  def typeName: String

  /**
   * The EventKey used to publish or subscribe an event
   *
   * @return an EventKey formed by combination of prefix and eventName of an event
   */
  def eventKey: EventKey = EventKey(source, eventName)

  /**
   * A common toString method for all concrete implementation
   *
   * @return the string representation of command
   */
  override def toString: String =
    s"$typeName(eventId=$eventId, source=$source, eventName=$eventName, eventTime=$eventTime, paramSet=$paramSet)"
}

object Event {
  private val mapper =
    TypeMapper[Seq[PbParameter], Set[Parameter[_]]] {
      _.map(Parameter.typeMapper2.toCustom).toSet
    } {
      _.map(Parameter.typeMapper2.toBase).toSeq
    }

  /**
   * TypeMapper definitions are required for to/from conversion PbEvent(Protobuf) <==> System, Observe event.
   */
  private[csw] implicit def typeMapper[T <: Event]: TypeMapper[PbEvent, T] = new TypeMapper[PbEvent, T] {
    override def toCustom(base: PbEvent): T = {
      val factory: (Id, Prefix, EventName, EventTime, Set[Parameter[_]]) ⇒ Any = base.eventType match {
        case PbEventType.SystemEvent     ⇒ SystemEvent.apply
        case PbEventType.ObserveEvent    ⇒ ObserveEvent.apply
        case PbEventType.Unrecognized(x) ⇒ throw new RuntimeException(s"unknown event type=[${base.eventType.toString} :$x]")
      }

      factory(
        Id(base.eventId),
        Prefix(base.source),
        EventName(base.name),
        base.eventTime.map(EventTime.typeMapper.toCustom).get,
        mapper.toCustom(base.paramSet)
      ).asInstanceOf[T]
    }

    override def toBase(custom: T): PbEvent = {
      val pbEventType = custom match {
        case _: ObserveEvent ⇒ PbEventType.ObserveEvent
        case _: SystemEvent  ⇒ PbEventType.SystemEvent
      }
      PbEvent()
        .withEventId(custom.eventId.id)
        .withSource(custom.source.prefix)
        .withName(custom.eventName.name)
        .withEventTime(EventTime.typeMapper.toBase(custom.eventTime))
        .withParamSet(mapper.toBase(custom.paramSet))
        .withEventType(pbEventType)
    }
  }

  /**
   * A helper method internally used to create an Event out of provided pbEvent
   *
   * @param pbEvent a PbEvent representing Event in protobuf
   * @return an Event mapped from PbEvent
   */
  def fromPb(pbEvent: PbEvent): Event = Event.typeMapper[Event].toCustom(pbEvent)

  def invalidEvent(eventKey: EventKey): SystemEvent =
    SystemEvent(eventKey.source, eventKey.eventName)
      .copy(eventId = Id("-1"), eventTime = EventTime(Instant.ofEpochMilli(-1)))
}

/**
 * Defines a system event. Constructor is private to ensure eventId is created internally to guarantee unique value.
 */
case class SystemEvent private (
    eventId: Id,
    source: Prefix,
    eventName: EventName,
    eventTime: EventTime,
    paramSet: Set[Parameter[_]]
) extends ParameterSetType[SystemEvent]
    with Event {

  /**
   * A java helper to construct SystemEvent
   */
  def this(source: Prefix, eventName: EventName) = this(Id(), source, eventName, EventTime(), Set.empty)

  /**
   * Create a new SystemEvent instance when a parameter is added or removed
   *
   * @param data set of parameters
   * @return a new instance of SystemEvent with new eventId, eventTime and provided data
   */
  override protected def create(data: Set[Parameter[_]]): SystemEvent =
    copy(eventId = Id(), eventTime = EventTime(), paramSet = data)

  /**
   * A helper method to create PbEvent out of this Event
   *
   * @return a protobuf representation of SystemEvent
   */
  def toPb: PbEvent = Event.typeMapper[SystemEvent].toBase(this)

  def isInvalid: Boolean = eventTime == EventTime(Instant.ofEpochMilli(-1))
}

object SystemEvent {

  // The default apply method is used only internally while reading the incoming json and de-serializing it to SystemEvent model
  private[messages] def apply(
      eventId: Id,
      source: Prefix,
      eventName: EventName,
      eventTime: EventTime,
      paramSet: Set[Parameter[_]]
  ) = new SystemEvent(eventId, source, eventName, eventTime, paramSet)

  /**
   * The apply method is used to create SystemEvent command by end-user. eventId is not accepted and will be created internally to guarantee unique value.
   *
   * @param source prefix representing source of the event
   * @param eventName the name of event
   * @return a new instance of SystemEvent with auto-generated eventId, eventTime and empty paramSet
   */
  def apply(source: Prefix, eventName: EventName): SystemEvent = apply(Id(), source, eventName, EventTime(), Set.empty)

  /**
   * The apply method is used to create SystemEvent command by end-user. eventId is not accepted and will be created internally to guarantee unique value.
   *
   * @param source prefix representing source of the event
   * @param eventName the name of event
   * @param paramSet an initial set of parameters (keys with values)
   * @return a new instance of SystemEvent with auto-generated eventId and eventTime
   */
  def apply(source: Prefix, eventName: EventName, paramSet: Set[Parameter[_]]): SystemEvent =
    apply(source, eventName).madd(paramSet)

  /**
   * Constructs from byte array containing Protobuf representation of SystemEvent
   *
   * @param pbEvent the protobuf representation of an event
   * @return a SystemEvent mapped from provided pbEvent
   */
  def fromPb(pbEvent: PbEvent): SystemEvent = Event.typeMapper[SystemEvent].toCustom(pbEvent)
}

/**
 * Defines an observe event. Constructor is private to ensure eventId is created internally to guarantee unique value.
 */
case class ObserveEvent private (
    eventId: Id,
    source: Prefix,
    eventName: EventName,
    eventTime: EventTime,
    paramSet: Set[Parameter[_]]
) extends ParameterSetType[ObserveEvent]
    with Event {

  /**
   * A java helper to construct ObserveEvent
   */
  def this(source: Prefix, eventName: EventName) = this(Id(), source, eventName, EventTime(), Set.empty)

  /**
   * Create a new ObserveEvent instance when a parameter is added or removed
   *
   * @param data set of parameters
   * @return a new instance of ObserveEvent with new eventId, eventTime and provided data
   */
  override protected def create(data: Set[Parameter[_]]): ObserveEvent =
    copy(eventId = Id(), eventTime = EventTime(), paramSet = data)

  /**
   * A helper method to create PbEvent out of this Event
   *
   * @return a protobuf representation of ObserveEvent
   */
  def toPb: PbEvent = Event.typeMapper[ObserveEvent].toBase(this)
}

object ObserveEvent {

  // The default apply method is used only internally while reading the incoming json and de-serializing it to ObserveEvent model
  private[messages] def apply(
      eventId: Id,
      source: Prefix,
      eventName: EventName,
      eventTime: EventTime,
      paramSet: Set[Parameter[_]]
  ) = new ObserveEvent(eventId, source, eventName, eventTime, paramSet)

  /**
   * The apply method is used to create ObserveEvent command by end-user. eventId is not accepted and will be created internally to guarantee unique value.
   *
   * @param source prefix representing source of the event
   * @param eventName the name of event
   * @return a new instance of ObserveEvent with auto-generated eventId, eventTime and empty paramSet
   */
  def apply(source: Prefix, eventName: EventName): ObserveEvent = apply(Id(), source, eventName, EventTime(), Set.empty)

  /**
   * The apply method is used to create ObserveEvent command by end-user. eventId is not accepted and will be created internally to guarantee unique value.
   *
   * @param source prefix representing source of the event
   * @param eventName the name of event
   * @param paramSet an initial set of parameters (keys with values)
   * @return a new instance of ObserveEvent with auto-generated eventId and eventTime
   */
  def apply(source: Prefix, eventName: EventName, paramSet: Set[Parameter[_]]): ObserveEvent =
    apply(source, eventName).madd(paramSet)

  /**
   * Constructs from byte array containing Protobuf representation of SystemEvent
   *
   * @param pbEvent the protobuf representation of an event
   * @return a ObserveEvent mapped from provided pbEvent
   */
  def fromPb(pbEvent: PbEvent): ObserveEvent = Event.typeMapper[ObserveEvent].toCustom(pbEvent)
}
