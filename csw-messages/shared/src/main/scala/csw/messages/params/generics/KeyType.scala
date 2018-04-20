package csw.messages.params.generics

import java.time.Instant

import scalapb.TypeMapper
import csw.messages.params.formats.JsonSupport
import csw.messages.params.models.Units.second
import csw.messages.params.models.{Units, _}
import csw.messages.params.pb.ItemsFactory
import csw_protobuf.keytype.PbKeyType
import csw_protobuf.parameter.PbParameter
import enumeratum.{Enum, EnumEntry, PlayJsonEnum}
import play.api.libs.json._

import scala.collection.immutable
import scala.reflect.ClassTag

/**
 * Generic marker class for creating various types of Keys.
 *
 * @tparam S the type of values that will sit against the key in Parameter
 */
sealed class KeyType[S: Format: ClassTag: ItemsFactory] extends EnumEntry with Serializable {
  private[messages] def paramFormat: Format[Parameter[S]]                 = Parameter[S]
  private[messages] def typeMapper: TypeMapper[PbParameter, Parameter[S]] = Parameter.typeMapper
}

/**
 * SimpleKeyType with a name. Holds instances of primitives such as char, int, String etc.
 *
 * @tparam S the type of values that will sit against the key in Parameter
 */
sealed class SimpleKeyType[S: Format: ClassTag: ItemsFactory] extends KeyType[S] {

  /**
   * Make a Key from provided name
   *
   * @param name represents keyName in Key
   * @return a Key[S] with NoUnits where S is the type of values that will sit against the key in Parameter
   */
  def make(name: String): Key[S] = new Key[S](name, this)
}

/**
 * A KeyType that allows name and unit to be specified during creation. Holds instances of primitives such as
 * char, int, String etc.
 *
 * @param defaultUnits applicable units
 * @tparam S the type of values that will sit against the key in Parameter
 */
sealed class SimpleKeyTypeWithUnits[S: Format: ClassTag: ItemsFactory](defaultUnits: Units) extends KeyType[S] {

  /**
   * Make a Key from provided name
   *
   * @param name represents keyName in Key
   * @return a Key[S] with defaultUnits where S is the type of values that will sit against the key in Parameter
   */
  def make(name: String): Key[S] = new Key[S](name, this, defaultUnits)
}

/**
 * A KeyType that holds array
 */
sealed class ArrayKeyType[S: Format: ClassTag](implicit x: ItemsFactory[ArrayData[S]]) extends SimpleKeyType[ArrayData[S]]

/**
 * A KeyType that holds Matrix
 */
sealed class MatrixKeyType[S: Format: ClassTag](implicit x: ItemsFactory[MatrixData[S]]) extends SimpleKeyType[MatrixData[S]]

//////////
/**
 * SimpleKeyType with a name for java Keys. Holds instances of primitives such as char, int, String etc.
 */
sealed class JSimpleKeyType[S: Format: ClassTag, T: ItemsFactory](implicit conversion: T ⇒ S) extends SimpleKeyType[S]

/**
 * A java KeyType that holds array
 */
sealed class JArrayKeyType[S: Format: ClassTag, T: ItemsFactory](
    implicit x: ItemsFactory[ArrayData[T]],
    conversion: T ⇒ S
) extends JSimpleKeyType[ArrayData[S], ArrayData[T]]

/**
 * A java KeyType that holds matrix
 */
sealed class JMatrixKeyType[S: Format: ClassTag, T: ItemsFactory](
    implicit x: ItemsFactory[MatrixData[T]],
    conversion: T ⇒ S
) extends JSimpleKeyType[MatrixData[S], MatrixData[T]]

/**
 * KeyTypes defined for consumption in Scala code
 */
object KeyType extends Enum[KeyType[_]] with PlayJsonEnum[KeyType[_]] {

  import JsonSupport._

  /**
   * values return a Seq of all KeyTypes provided by `csw-messages`
   */
  override def values: immutable.IndexedSeq[KeyType[_]] = findValues

  case object ChoiceKey extends KeyType[Choice] {
    def make(name: String, choices: Choices): GChoiceKey = new GChoiceKey(name, this, choices)
    def make(name: String, firstChoice: Choice, restChoices: Choice*): GChoiceKey =
      new GChoiceKey(name, this, Choices(restChoices.toSet + firstChoice))
  }

  case object RaDecKey     extends SimpleKeyType[RaDec]
  case object StringKey    extends SimpleKeyType[String]
  case object StructKey    extends SimpleKeyType[Struct]
  case object TimestampKey extends SimpleKeyTypeWithUnits[Instant](second)

  //scala
  case object BooleanKey extends SimpleKeyType[Boolean]
  case object CharKey    extends SimpleKeyType[Char]

  case object ByteKey   extends SimpleKeyType[Byte]
  case object ShortKey  extends SimpleKeyType[Short]
  case object LongKey   extends SimpleKeyType[Long]
  case object IntKey    extends SimpleKeyType[Int]
  case object FloatKey  extends SimpleKeyType[Float]
  case object DoubleKey extends SimpleKeyType[Double]

  case object ByteArrayKey   extends ArrayKeyType[Byte]
  case object ShortArrayKey  extends ArrayKeyType[Short]
  case object LongArrayKey   extends ArrayKeyType[Long]
  case object IntArrayKey    extends ArrayKeyType[Int]
  case object FloatArrayKey  extends ArrayKeyType[Float]
  case object DoubleArrayKey extends ArrayKeyType[Double]

  case object ByteMatrixKey   extends MatrixKeyType[Byte]
  case object ShortMatrixKey  extends MatrixKeyType[Short]
  case object LongMatrixKey   extends MatrixKeyType[Long]
  case object IntMatrixKey    extends MatrixKeyType[Int]
  case object FloatMatrixKey  extends MatrixKeyType[Float]
  case object DoubleMatrixKey extends MatrixKeyType[Double]

  //java
  case object JBooleanKey extends JSimpleKeyType[java.lang.Boolean, Boolean]
  case object JCharKey    extends JSimpleKeyType[java.lang.Character, Char]

  case object JByteKey   extends JSimpleKeyType[java.lang.Byte, Byte]
  case object JShortKey  extends JSimpleKeyType[java.lang.Short, Short]
  case object JLongKey   extends JSimpleKeyType[java.lang.Long, Long]
  case object JIntKey    extends JSimpleKeyType[java.lang.Integer, Int]
  case object JFloatKey  extends JSimpleKeyType[java.lang.Float, Float]
  case object JDoubleKey extends JSimpleKeyType[java.lang.Double, Double]

  case object JByteArrayKey   extends JArrayKeyType[java.lang.Byte, Byte]
  case object JShortArrayKey  extends JArrayKeyType[java.lang.Short, Short]
  case object JLongArrayKey   extends JArrayKeyType[java.lang.Long, Long]
  case object JIntArrayKey    extends JArrayKeyType[java.lang.Integer, Int]
  case object JFloatArrayKey  extends JArrayKeyType[java.lang.Float, Float]
  case object JDoubleArrayKey extends JArrayKeyType[java.lang.Double, Double]

  case object JByteMatrixKey   extends JMatrixKeyType[java.lang.Byte, Byte]
  case object JShortMatrixKey  extends JMatrixKeyType[java.lang.Short, Short]
  case object JLongMatrixKey   extends JMatrixKeyType[java.lang.Long, Long]
  case object JIntMatrixKey    extends JMatrixKeyType[java.lang.Integer, Int]
  case object JFloatMatrixKey  extends JMatrixKeyType[java.lang.Float, Float]
  case object JDoubleMatrixKey extends JMatrixKeyType[java.lang.Double, Double]

  implicit def format2[T]: Format[KeyType[T]] = implicitly[Format[KeyType[_]]].asInstanceOf[Format[KeyType[T]]]

  implicit val typeMapper: TypeMapper[PbKeyType, KeyType[_]] =
    TypeMapper[PbKeyType, KeyType[_]](x ⇒ KeyType.withName(x.toString()))(x ⇒ PbKeyType.fromName(x.toString).get)
}

/**
 * KeyTypes defined for consumption in Java code
 */
object JKeyTypes {
  val ChoiceKey    = KeyType.ChoiceKey
  val RaDecKey     = KeyType.RaDecKey
  val StringKey    = KeyType.StringKey
  val StructKey    = KeyType.StructKey
  val TimestampKey = KeyType.TimestampKey

  val BooleanKey = KeyType.JBooleanKey
  val CharKey    = KeyType.JCharKey

  val ByteKey   = KeyType.JByteKey
  val ShortKey  = KeyType.JShortKey
  val LongKey   = KeyType.JLongKey
  val IntKey    = KeyType.JIntKey
  val FloatKey  = KeyType.JFloatKey
  val DoubleKey = KeyType.JDoubleKey

  val ByteArrayKey   = KeyType.JByteArrayKey
  val ShortArrayKey  = KeyType.JShortArrayKey
  val LongArrayKey   = KeyType.JLongArrayKey
  val IntArrayKey    = KeyType.JIntArrayKey
  val FloatArrayKey  = KeyType.JFloatArrayKey
  val DoubleArrayKey = KeyType.JDoubleArrayKey

  val ByteMatrixKey   = KeyType.JByteMatrixKey
  val ShortMatrixKey  = KeyType.JShortMatrixKey
  val LongMatrixKey   = KeyType.JLongMatrixKey
  val IntMatrixKey    = KeyType.JIntMatrixKey
  val FloatMatrixKey  = KeyType.JFloatMatrixKey
  val DoubleMatrixKey = KeyType.JDoubleMatrixKey
}
