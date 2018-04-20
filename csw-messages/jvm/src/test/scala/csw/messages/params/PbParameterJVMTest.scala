//package csw.messages.params.pb
//
//import java.time.Instant
//
//import csw.messages.params.generics.KeyType.IntMatrixKey
//import csw.messages.params.generics.{JKeyTypes, KeyType, Parameter}
//import csw.messages.params.models._
//import csw_protobuf.parameter.PbParameter
//import csw_protobuf.parameter_types._
//import csw_protobuf.radec.PbRaDec
//import org.scalatest.{FunSuite, Matchers}
//
//import scala.collection.JavaConverters.iterableAsScalaIterableConverter
//
//// DEOPSCSW-297: Merge protobuf branch in master
//class PbParameterJVMTest extends FunSuite with Matchers {
//
//  test("should able to create Boolean Items") {
//    import csw_protobuf.parameter_types.ParameterTypes
//    val booleanItems: ParameterTypes.BooleanItems =
//      ParameterTypes.BooleanItems.newBuilder().addValues(true).addValues(false).build()
//
//    val parsedBooleanItems = BooleanItems.parseFrom(booleanItems.toByteString.toByteArray)
//
//    booleanItems.getValuesList.asScala.toSeq shouldBe parsedBooleanItems.values
//  }
//
//}
