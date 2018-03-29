import enumeratum._

import scala.collection.immutable

sealed trait ObservingMode extends EnumEntry

object ObservingMode extends Enum[ObservingMode] {

  override def values: immutable.IndexedSeq[ObservingMode] = findValues

  case object DarkNight  extends ObservingMode
  case object ClearSkies extends ObservingMode
}
