package csw.messages.params.generics

import java.nio.file.{Files, Paths}
import java.time.Instant

import csw.messages.params.generics.KeyType.{
  ByteArrayKey,
  ByteMatrixKey,
  ChoiceKey,
  DoubleMatrixKey,
  FloatMatrixKey,
  IntMatrixKey,
  LongMatrixKey,
  ShortMatrixKey,
  StructKey
}
import csw.messages.params.models.Units.{degree, encoder, meter, second, NoUnits}
import csw.messages.params.models._
import org.scalatest.{FunSpec, Matchers}

// DEOPSCSW-183: Configure attributes and values
// DEOPSCSW-185: Easy to Use Syntax/Api
// DEOPSCSW-188: Efficient Serialization to/from JSON
// DEOPSCSW-184: Change configurations - attributes and values
class KeyParameterJVMTest extends FunSpec with Matchers {

  private val s1: String = "encoder"
  private val s2: String = "filter"

  // DEOPSCSW-186: Binary value payload
  describe("test ByteArrayKey") {
    val a1: Array[Byte] = Array[Byte](1, 2, 3, 4, 5)
    val a2: Array[Byte] = Array[Byte](10, 20, 30, 40, 50)

    val la1 = ArrayData(a1)
    val la2 = ArrayData(a2)
    val lk  = KeyType.ByteArrayKey.make(s1)

    val listIn = Array(la1, la2)

    //DEOPSCSW-186: Binary value payload
    it("should able to create parameter representing binary image") {
      val keyName                        = "imageKey"
      val imageKey: Key[ArrayData[Byte]] = ByteArrayKey.make(keyName)

      val imgPath  = Paths.get(getClass.getResource("/smallBinary.bin").getPath)
      val imgBytes = Files.readAllBytes(imgPath)

      val binaryImgData: ArrayData[Byte]          = ArrayData.fromArray(imgBytes)
      val binaryParam: Parameter[ArrayData[Byte]] = imageKey -> binaryImgData withUnits encoder

      binaryParam.head shouldBe binaryImgData
      binaryParam.value(0) shouldBe binaryImgData
      binaryParam.units shouldBe encoder
      binaryParam.keyName shouldBe keyName
      binaryParam.size shouldBe 1
      binaryParam.keyType shouldBe KeyType.ByteArrayKey

    }

  }

}
