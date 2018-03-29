import enumeratum._

import scala.collection.immutable

sealed trait SequencerId extends EnumEntry

object SequencerId extends Enum[SequencerId] {
  override def values: immutable.IndexedSeq[SequencerId] = findValues

  case object Ocs extends SequencerId
  case object Iris extends SequencerId
  case object Tcs  extends SequencerId
}
