package csw.messages.params.pb

import csw_protobuf.ParameterTypes
import csw_protobuf.parameter_types._
import org.scalatest.{FunSuite, Matchers}

import scala.collection.JavaConverters.iterableAsScalaIterableConverter

// DEOPSCSW-297: Merge protobuf branch in master
class PbParameterJVMTest extends FunSuite with Matchers {

  test("should able to create Boolean Items") {
    val booleanItems: ParameterTypes.BooleanItems =
      ParameterTypes.BooleanItems.newBuilder().addValues(true).addValues(false).build()

    val parsedBooleanItems = BooleanItems.parseFrom(booleanItems.toByteString.toByteArray)

    booleanItems.getValuesList.asScala.toSeq shouldBe parsedBooleanItems.values
  }

}
