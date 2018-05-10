package tmt.sequencer.models

import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._

case class Boo(x: Int)

class CommandTest extends org.scalatest.FunSuite {
  test("demo") {
    val command = Command(Id("1"), "setup-iris", List(1, 2))
    println("**********************")
    val jsonRep = command.asJson.noSpaces
    println(jsonRep)
    println("**********************")
    val decodedCommand = decode[Command](jsonRep)
    println(decodedCommand)
  }

  test("boo") {
    println(decode[Boo]((Boo(100).asJson.noSpaces)))
  }
}
