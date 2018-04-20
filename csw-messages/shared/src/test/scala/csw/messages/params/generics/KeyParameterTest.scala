package csw.messages.params.generics

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
class KeyParameterTest extends FunSpec with Matchers {

  private val s1: String = "encoder"
  private val s2: String = "filter"

  describe("basic key tests") {
    val k1: Key[Int] = KeyType.IntKey.make(s1)
    val k2: Key[Int] = KeyType.IntKey.make(s2)
    it("should have correct name") {
      assert(k1.keyName.equals(s1))
    }

    val k3: Key[Int] = KeyType.IntKey.make(s1)

    it("should have equality based on name") {
      assert(k3 == k1)
      assert(k3 != k2)
      assert(k1 != k2)
    }
  }

  describe("test booleanKey") {

    val tval = false
    val bk   = KeyType.BooleanKey.make(s1)

    it("should allow single val") {
      val ii = bk.set(tval)
      ii.values should be(Array(tval))
      ii.get(0).get should equal(tval)
    }

    val listIn = Array(false, true)

    // DEOPSCSW-190: Implement Unit Support
    it("should work with list, withUnits") {
      val ii = bk.set(listIn).withUnits(degree)
      ii.units should be(degree)
      ii.value(1) should equal(listIn(1))
      ii.values should equal(listIn)
    }

    // DEOPSCSW-190: Implement Unit Support
    it("should work with list, units") {
      val ii = bk.set(listIn, degree)
      ii.units should be(degree)
      ii.value(1) should equal(listIn(1))
      ii.values should equal(listIn)
    }
  }

  describe("test charItem") {
    val tval = 'K'
    val lk   = KeyType.CharKey.make(s1)

    it("should allow single val") {
      val li = lk.set(tval)
      li.values should be(Array(tval))
      li.head should be(tval)
      li.get(0).get should equal(tval)
    }

    val listIn = Array[Char]('K', 'G')

    // DEOPSCSW-190: Implement Unit Support
    it("should work with list, withUnits") {
      val li = lk.set(listIn).withUnits(degree)
      li.units should be(degree)
      li.value(1) should equal(listIn(1))
      li.values should equal(listIn)
    }

    it("should work with list, units") {
      val li = lk.set(listIn, degree)
      li.units should be(degree)
      li.value(1) should equal(listIn(1))
      li.values should equal(listIn)
    }
  }

  // DEOPSCSW-186: Binary value payload
  describe("test ByteItem") {
    val tval: Byte = 123
    val lk         = KeyType.ByteKey.make(s1)

    it("should allow single val") {
      val li = lk.set(tval)
      li.values should be(Array(tval))
      li.head should be(tval)
      li.get(0).get should equal(tval)
    }

    val byteIn = Array[Byte](121, 122)

    it("should work with list, withUnits") {
      val li = lk.set(byteIn).withUnits(degree)
      li.units should be(degree)
      li.value(0) should equal(byteIn(0))
      li.value(1) should equal(byteIn(1))
      li.values should equal(byteIn)
    }

    it("should work with list, units") {
      val li = lk.set(byteIn, degree)
      li.units should be(degree)
      li.value(1) should equal(byteIn(1))
      li.values should equal(byteIn)
    }
  }

  // DEOPSCSW-186: Binary value payload
  describe("test ByteArrayKey") {
    val a1: Array[Byte] = Array[Byte](1, 2, 3, 4, 5)
    val a2: Array[Byte] = Array[Byte](10, 20, 30, 40, 50)

    val la1 = ArrayData(a1)
    val la2 = ArrayData(a2)
    val lk  = KeyType.ByteArrayKey.make(s1)

    it("should test single item") {
      val di: Parameter[ArrayData[Byte]] = lk.set(la1)
      di.values should equal(Array(la1))
      di.head should be(la1)
      di.get(0).get should equal(la1)
    }

    val listIn = Array(la1, la2)

    // DEOPSCSW-190: Implement Unit Support
    it("should test with list, withUnits") {
      val li2: Parameter[ArrayData[Byte]] = lk.set(listIn).withUnits(degree)
      li2.units should be(degree)
      li2.value(1) should equal(listIn(1))
      li2.values should equal(listIn)
    }

    // DEOPSCSW-190: Implement Unit Support
    it("should test with list, units") {
      val li2 = lk.set(listIn, degree)
      li2.units should be theSameInstanceAs degree
      li2.value(1) should equal(listIn(1))
      li2.values should equal(listIn)
    }

    // DEOPSCSW-190: Implement Unit Support
    it("should test using one array with and without units") {
      var li2 = lk.set(a1) // Uses implicit to create from long array
      li2.head should equal(la1)
      li2 = lk.set(a2).withUnits(degree)
      li2.units should be theSameInstanceAs degree
      li2.head should equal(la2)
    }

    it("should test using var args") {
      val li3: Parameter[ArrayData[Byte]] = lk.set(la1, la2)
      li3.value(1) should equal(la2)
      li3.values should equal(listIn)

      val a: Array[Byte] = Array[Byte](1, 2, 3)
      val b: Array[Byte] = Array[Byte](10, 20, 30)
      val c: Array[Byte] = Array[Byte](100, 101, 102)

      val li4: Parameter[ArrayData[Byte]] = lk.set(a, b, c).withUnits(meter)
      li4.values.length should be(3)
      li4.value(2) should equal(ArrayData(c))
    }

    // DEOPSCSW-186: Binary value payload
//    it("should able to create parameter representing binary image") {
//      val keyName                        = "imageKey"
//      val imageKey: Key[ArrayData[Byte]] = ByteArrayKey.make(keyName)
//
//      val imgPath  = Paths.get(getClass.getResource("/smallBinary.bin").getPath)
//      val imgBytes = Files.readAllBytes(imgPath)
//
//      val binaryImgData: ArrayData[Byte]          = ArrayData.fromArray(imgBytes)
//      val binaryParam: Parameter[ArrayData[Byte]] = imageKey -> binaryImgData withUnits encoder
//
//      binaryParam.head shouldBe binaryImgData
//      binaryParam.value(0) shouldBe binaryImgData
//      binaryParam.units shouldBe encoder
//      binaryParam.keyName shouldBe keyName
//      binaryParam.size shouldBe 1
//      binaryParam.keyType shouldBe KeyType.ByteArrayKey
//
//    }

  }

  // DEOPSCSW-186: Binary value payload
  describe("test byteMatrixKey") {
    val m1: Array[Array[Byte]] = Array(Array[Byte](1, 2, 3), Array[Byte](4, 5, 6), Array[Byte](7, 8, 9))
    val m2: Array[Array[Byte]] = Array(Array[Byte](1, 2, 3, 4, 5), Array[Byte](10, 20, 30, 40, 50))

    val lm1 = MatrixData.fromArrays(m1)
    val lm2 = MatrixData.fromArrays(m2)
    val dk  = ByteMatrixKey.make(s1)

    it("should work with a single item") {
      val di = dk.set(lm1)
      di.values should equal(Array(lm1))
      di.head should equal(lm1)
      di.get(0).get should equal(lm1)
    }

    val listIn = Array(lm1, lm2)

    // DEOPSCSW-190: Implement Unit Support
    it("should work with list and withUnits") {
      val di = dk.set(listIn).withUnits(degree)
      di.units should be theSameInstanceAs degree
      di.value(1) should equal(listIn(1))
      di.values should equal(listIn)
    }

    // DEOPSCSW-190: Implement Unit Support
    it("should work with list and units") {
      val di = dk.set(listIn, degree)
      di.units should be theSameInstanceAs degree
      di.value(1) should equal(listIn(1))
      di.values should equal(listIn)
    }

    // DEOPSCSW-190: Implement Unit Support
    it("work with one matrix without and with units") {
      var di = dk.set(m1) // This is an implicit
      di.head should equal(lm1)
      di = dk.set(m1).withUnits(degree)
      di.units should be theSameInstanceAs degree
      di.head should equal(lm1)
    }

    // DEOPSCSW-190: Implement Unit Support
    it("work with varargs") {
      val di = dk.set(lm1, lm2).withUnits(second)
      di.units should be theSameInstanceAs second
      di.value(1) should equal(lm2)
      di.values should equal(listIn)
    }

    // DEOPSCSW-190: Implement Unit Support
    it("work with varargs as arrays") {
      val di = dk.set(m1, m2).withUnits(meter)
      di.units should be theSameInstanceAs meter
      di.value(0) should equal(lm1)
      di.values should equal(listIn)
    }
  }

  describe("test ShortItem") {
    val tval: Short = 1234
    val lk          = KeyType.ShortKey.make(s1)

    it("should allow single val") {
      val li = lk.set(tval)
      li.values should be(Array(tval))
      li.head should be(tval)
      li.get(0).get should equal(tval)
    }

    val listIn = Array[Short](123, 456)

    it("should work with list, withUnits") {
      val li = lk.set(listIn).withUnits(degree)
      li.units should be(degree)
      li.value(0) should equal(listIn(0))
      li.value(1) should equal(listIn(1))
      li.values should equal(listIn)
    }

    it("should work with list, units") {
      val li = lk.set(listIn, degree)
      li.units should be(degree)
      li.value(1) should equal(listIn(1))
      li.values should equal(listIn)
    }
  }

  describe("test shortArrayKey") {
    val a1 = Array[Short](1, 2, 3, 4, 5)
    val a2 = Array[Short](10, 20, 30, 40, 50)

    val la1 = ArrayData(a1)
    val la2 = ArrayData(a2)
    val lk  = KeyType.ShortArrayKey.make(s1)

    it("should test single item") {
      val di = lk.set(la1)
      di.values should equal(Array(la1))
      di.head should be(la1)
      di.get(0).get should equal(la1)
    }

    val listIn = Array(la1, la2)

    it("should test with list, withUnits") {
      val li2 = lk.set(listIn).withUnits(degree)
      li2.units should be(degree)
      li2.value(1) should equal(listIn(1))
      li2.values should equal(listIn)
    }

    it("should test with list, units") {
      val li2 = lk.set(listIn, degree)
      li2.units should be theSameInstanceAs degree
      li2.value(1) should equal(listIn(1))
      li2.values should equal(listIn)
    }

    it("should test using one array with and without units") {
      var li2 = lk.set(a1) // Uses implicit to create from int array
      li2.head should equal(la1)
      li2 = lk.set(a2).withUnits(degree)
      li2.units should be theSameInstanceAs degree
      li2.head should equal(la2)
    }

    it("should test using var args") {
      val li3 = lk.set(la1, la2)
      li3.value(1) should equal(la2)
      li3.values should equal(listIn)

      val a: Array[Short] = Array[Short](1, 2, 3)
      val b: Array[Short] = Array[Short](10, 20, 30)
      val c: Array[Short] = Array[Short](100, 200, 300)

      val li4 = lk.set(a, b, c).withUnits(meter)
      li4.values.length should be(3)
      li4.value(2) should equal(ArrayData(c))
    }
  }

  describe("test shortMatrixKey") {
    val m1: Array[Array[Short]] = Array(Array[Short](1, 2, 3), Array[Short](4, 5, 6), Array[Short](7, 8, 9))
    val m2: Array[Array[Short]] = Array(Array[Short](1, 2, 3, 4, 5), Array[Short](10, 20, 30, 40, 50))

    val lm1 = MatrixData.fromArrays(m1)
    val lm2 = MatrixData.fromArrays(m2)
    val dk  = ShortMatrixKey.make(s1)

    it("should work with a single item") {
      val di = dk.set(lm1)
      di.values should equal(Array(lm1))
      di.head should equal(lm1)
      di.get(0).get should equal(lm1)
    }

    val listIn = Array(lm1, lm2)

    it("should work with list and withUnits") {
      val di = dk.set(listIn).withUnits(degree)
      di.units should be theSameInstanceAs degree
      di.value(1) should equal(listIn(1))
      di.values should equal(listIn)
    }

    it("should work with list and units") {
      val di = dk.set(listIn, degree)
      di.units should be theSameInstanceAs degree
      di.value(1) should equal(listIn(1))
      di.values should equal(listIn)
    }

    it("work with one matrix without and with units") {
      var di = dk.set(m1) // This is an implicit
      di.head should equal(lm1)
      di = dk.set(m1).withUnits(degree)
      di.units should be theSameInstanceAs degree
      di.head should equal(lm1)
    }

    it("work with varargs") {
      val di = dk.set(lm1, lm2).withUnits(second)
      di.units should be theSameInstanceAs second
      di.value(1) should equal(lm2)
      di.values should equal(listIn)
    }

    it("work with varargs as arrays") {
      val di = dk.set(m1, m2).withUnits(meter)
      di.units should be theSameInstanceAs meter
      di.value(0) should equal(lm1)
      di.values should equal(listIn)
    }
  }

  describe("test IntItem") {
    val tval: Int = 1234
    val lk        = KeyType.IntKey.make(s1)

    it("should allow single val") {
      val li = lk.set(tval)
      li.values should be(Array(tval))
      li.head should be(tval)
      li.get(0).get should equal(tval)
    }

    val listIn = Array[Int](123, 456)

    it("should work with list, withUnits") {
      val li = lk.set(listIn).withUnits(degree)
      li.units should be(degree)
      li.value(0) should equal(listIn(0))
      li.value(1) should equal(listIn(1))
      li.values should equal(listIn)
    }

    it("should work with list, units") {
      val li = lk.set(listIn, degree)
      li.units should be(degree)
      li.value(1) should equal(listIn(1))
      li.values should equal(listIn)
    }
  }

  describe("test intArrayKey") {
    val a1 = Array[Int](1, 2, 3, 4, 5)
    val a2 = Array[Int](10, 20, 30, 40, 50)

    val la1 = ArrayData(a1)
    val la2 = ArrayData(a2)
    val lk  = KeyType.IntArrayKey.make(s1)

    it("should test single item") {
      val di = lk.set(la1)
      di.values should equal(Array(la1))
      di.head should be(la1)
      di.get(0).get should equal(la1)
    }

    val listIn = Array(la1, la2)

    it("should test with list, withUnits") {
      val li2 = lk.set(listIn).withUnits(degree)
      li2.units should be(degree)
      li2.value(1) should equal(listIn(1))
      li2.values should equal(listIn)
    }

    it("should test with list, units") {
      val li2 = lk.set(listIn, degree)
      li2.units should be theSameInstanceAs degree
      li2.value(1) should equal(listIn(1))
      li2.values should equal(listIn)
    }

    it("should test using one array with and without units") {
      var li2 = lk.set(a1) // Uses implicit to create from int array
      li2.head should equal(la1)
      li2 = lk.set(a2).withUnits(degree)
      li2.units should be theSameInstanceAs degree
      li2.head should equal(la2)
    }

    it("should test using var args") {
      val li3 = lk.set(la1, la2)
      li3.value(1) should equal(la2)
      li3.values should equal(listIn)

      val a: Array[Int] = Array(1, 2, 3)
      val b: Array[Int] = Array(10, 20, 30)
      val c: Array[Int] = Array(100, 200, 300)

      val li4 = lk.set(a, b, c).withUnits(meter)
      li4.values.length should be(3)
      li4.value(2) should equal(ArrayData(c))
    }
  }

  describe("test intMatrixKey") {
    val m1: Array[Array[Int]] = Array(Array(1, 2, 3), Array(4, 5, 6), Array(7, 8, 9))
    val m2: Array[Array[Int]] = Array(Array(1, 2, 3, 4, 5), Array(10, 20, 30, 40, 50))

    val lm1 = MatrixData.fromArrays(m1)
    val lm2 = MatrixData.fromArrays(m2)
    val dk  = IntMatrixKey.make(s1)

    it("should work with a single item") {
      val di = dk.set(lm1)
      di.values should equal(Array(lm1))
      di.head should equal(lm1)
      di.get(0).get should equal(lm1)
    }

    val listIn = Array(lm1, lm2)

    it("should work with list and withUnits") {
      val di = dk.set(listIn).withUnits(degree)
      di.units should be theSameInstanceAs degree
      di.value(1) should equal(listIn(1))
      di.values should equal(listIn)
    }

    it("should work with list and units") {
      val di = dk.set(listIn, degree)
      di.units should be theSameInstanceAs degree
      di.value(1) should equal(listIn(1))
      di.values should equal(listIn)
    }

    it("work with one matrix without and with units") {
      var di = dk.set(m1) // This is an implicit
      di.head should equal(lm1)
      di = dk.set(m1).withUnits(degree)
      di.units should be theSameInstanceAs degree
      di.head should equal(lm1)
    }

    it("work with varargs") {
      val di = dk.set(lm1, lm2).withUnits(second)
      di.units should be theSameInstanceAs second
      di.value(1) should equal(lm2)
      di.values should equal(listIn)
    }

    it("work with varargs as arrays") {
      val di = dk.set(m1, m2).withUnits(meter)
      di.units should be theSameInstanceAs meter
      di.value(0) should equal(lm1)
      di.values should equal(listIn)
    }
  }

  describe("test longKey") {
    val lval          = 1234L
    val lk: Key[Long] = KeyType.LongKey.make(s1)

    it("should allow single val") {
      val li = lk.set(lval)
      li.values should be(Array(lval))
      li.get(0).get should equal(lval)
    }

    val listIn = Array[Long](123L, 456L)

    it("should work with list, withUnits") {
      val li = lk.set(listIn).withUnits(degree)
      li.units should be(degree)
      li.value(1) should equal(listIn(1))
      li.values should equal(listIn)
    }

    it("should work with list, units") {
      val li = lk.set(listIn, degree)
      li.units should be(degree)
      li.value(1) should equal(listIn(1))
      li.values should equal(listIn)
    }
  }

  describe("test LongArrayKey") {
    val a1: Array[Long] = Array(1, 2, 3, 4, 5)
    val a2: Array[Long] = Array(10, 20, 30, 40, 50)

    val la1: ArrayData[Long]     = ArrayData(a1)
    val la2: ArrayData[Long]     = ArrayData(a2)
    val lk: Key[ArrayData[Long]] = KeyType.LongArrayKey.make(s1)

    it("should test single item") {
      val di: Parameter[ArrayData[Long]] = lk.set(la1)
      di.values should equal(Array(la1))
      di.head should be(la1)
      di.get(0).get should equal(la1)
    }

    val listIn = Array(la1, la2)

    it("should test with list, withUnits") {
      val li2: Parameter[ArrayData[Long]] = lk.set(listIn).withUnits(degree)
      li2.units should be(degree)
      li2.value(1) should equal(listIn(1))
      li2.values should equal(listIn)
    }

    it("should test with list, units") {
      val li2 = lk.set(listIn, degree)
      li2.units should be theSameInstanceAs degree
      li2.value(1) should equal(listIn(1))
      li2.values should equal(listIn)
    }

    it("should test using one array with and without units") {
      var li2 = lk.set(a1) // Uses implicit to create from long array
      li2.head should equal(la1)
      li2 = lk.set(a2).withUnits(degree)
      li2.units should be theSameInstanceAs degree
      li2.head should equal(la2)
    }

    it("should test using var args") {
      val li3: Parameter[ArrayData[Long]] = lk.set(la1, la2)
      li3.value(1) should equal(la2)
      li3.values should equal(listIn)

      val a: Array[Long] = Array(1, 2, 3)
      val b: Array[Long] = Array(10, 20, 30)
      val c: Array[Long] = Array(100, 200, 300)

      val li4: Parameter[ArrayData[Long]] = lk.set(a, b, c).withUnits(meter)
      li4.values.length should be(3)
      li4.value(2) should equal(ArrayData(c))
    }
  }

  describe("test longMatrixKey") {
    val m1: Array[Array[Long]] = Array(Array(1, 2, 3), Array(4, 5, 6), Array(7, 8, 9))
    val m2: Array[Array[Long]] = Array(Array(1, 2, 3, 4, 5), Array(10, 20, 30, 40, 50))

    val lm1: MatrixData[Long]     = MatrixData.fromArrays(m1)
    val lm2: MatrixData[Long]     = MatrixData.fromArrays(m2)
    val dk: Key[MatrixData[Long]] = LongMatrixKey.make(s1)

    it("should work with a single item") {
      val di = dk.set(lm1)
      di.values should equal(Array(lm1))
      di.head should equal(lm1)
      di.get(0).get should equal(lm1)
    }

    val listIn = Array(lm1, lm2)

    it("should work with list and withUnits") {
      val di = dk.set(listIn).withUnits(degree)
      di.units should be theSameInstanceAs degree
      di.value(1) should equal(listIn(1))
      di.values should equal(listIn)
    }

    it("should work with list and units") {
      val di = dk.set(listIn, degree)
      di.units should be theSameInstanceAs degree
      di.value(1) should equal(listIn(1))
      di.values should equal(listIn)
    }

    it("work with one matrix without and with units") {
      var di = dk.set(m1) // This is an implicit
      di.head should equal(lm1)
      di = dk.set(m1).withUnits(degree)
      di.units should be theSameInstanceAs degree
      di.head should equal(lm1)
    }

    it("work with varargs") {
      val di = dk.set(lm1, lm2).withUnits(second)
      di.units should be theSameInstanceAs second
      di.value(1) should equal(lm2)
      di.values should equal(listIn)
    }

    it("work with varargs as arrays") {
      val di = dk.set(m1, m2).withUnits(meter)
      di.units should be theSameInstanceAs meter
      di.value(0) should equal(lm1)
      di.values should equal(listIn)
    }
  }

  describe("test floatItem") {
    val tval: Float = 123.456f
    val lk          = KeyType.FloatKey.make(s1)

    it("should allow single val") {
      val li = lk.set(tval)
      li.values should be(Array(tval))
      li.head should be(tval)
      li.get(0).get should equal(tval)
    }

    val listIn = Array[Float](123.0f, 456.0f)

    it("should work with list, withUnits") {
      val li = lk.set(listIn).withUnits(degree)
      li.units should be(degree)
      li.value(1) should equal(listIn(1))
      li.values should equal(listIn)
    }

    it("should work with list, units") {
      val li = lk.set(listIn, degree)
      li.units should be(degree)
      li.value(1) should equal(listIn(1))
      li.values should equal(listIn)
    }
  }

  describe("test floatArrayKey") {
    val a1 = Array[Float](1.0f, 2.0f, 3.0f, 4.0f, 5.0f)
    val a2 = Array[Float](10.0f, 20.0f, 30.0f, 40.0f, 50.0f)

    val la1 = ArrayData(a1)
    val la2 = ArrayData(a2)
    val lk  = KeyType.FloatArrayKey.make(s1)

    it("should test single item") {
      val di = lk.set(la1)
      di.values should equal(Array(la1))
      di.head should be(la1)
      di.get(0).get should equal(la1)
    }

    val listIn = Array(la1, la2)

    it("should test with list, withUnits") {
      val li2 = lk.set(listIn).withUnits(degree)
      li2.units should be(degree)
      li2.value(1) should equal(listIn(1))
      li2.values should equal(listIn)
    }

    it("should test with list, units") {
      val li2 = lk.set(listIn, degree)
      li2.units should be theSameInstanceAs degree
      li2.value(1) should equal(listIn(1))
      li2.values should equal(listIn)
    }

    it("should test using one array with and without units") {
      var li2 = lk.set(a1) // Uses implicit to create from long array
      li2.head should equal(la1)
      li2 = lk.set(a2).withUnits(degree)
      li2.units should be theSameInstanceAs degree
      li2.head should equal(la2)
    }

    it("should test using var args") {
      val li3 = lk.set(la1, la2)
      li3.value(1) should equal(la2)
      li3.values should equal(listIn)

      val a: Array[Float] = Array(1f, 2f, 3f)
      val b: Array[Float] = Array(10f, 20f, 30f)
      val c: Array[Float] = Array(100f, 200f, 300f)

      val li4 = lk.set(a, b, c).withUnits(meter)
      li4.values.length should be(3)
      li4.value(2) should equal(ArrayData(c))
    }
  }

  describe("test floatMatrixKey") {
    val m1: Array[Array[Float]] = Array(Array(1f, 2f, 3f), Array(4f, 5f, 6f), Array(7f, 8f, 9f))
    val m2: Array[Array[Float]] = Array(Array(1f, 2f, 3f, 4f, 5f), Array(10f, 20f, 30f, 40f, 50f))

    val lm1 = MatrixData.fromArrays(m1)
    val lm2 = MatrixData.fromArrays(m2)
    val dk  = FloatMatrixKey.make(s1)

    it("should work with a single item") {
      val di = dk.set(lm1)
      di.values should equal(Array(lm1))
      di.head should equal(lm1)
      di.get(0).get should equal(lm1)
    }

    val listIn = Array(lm1, lm2)

    it("should work with list and withUnits") {
      val di = dk.set(listIn).withUnits(degree)
      di.units should be theSameInstanceAs degree
      di.value(1) should equal(listIn(1))
      di.values should equal(listIn)
    }

    it("should work with list and units") {
      val di = dk.set(listIn, degree)
      di.units should be theSameInstanceAs degree
      di.value(1) should equal(listIn(1))
      di.values should equal(listIn)
    }

    it("work with one matrix without and with units") {
      var di = dk.set(m1) // This is an implicit
      di.head should equal(lm1)
      di = dk.set(m1).withUnits(degree)
      di.units should be theSameInstanceAs degree
      di.head should equal(lm1)
    }

    it("work with varargs") {
      val di = dk.set(lm1, lm2).withUnits(second)
      di.units should be theSameInstanceAs second
      di.value(1) should equal(lm2)
      di.values should equal(listIn)
    }

    it("work with varargs as arrays") {
      val di = dk.set(m1, m2).withUnits(meter)
      di.units should be theSameInstanceAs meter
      di.value(0) should equal(lm1)
      di.values should equal(listIn)
    }
  }

  describe("test doubleItem") {
    val tval: Double = 123.456
    val lk           = KeyType.DoubleKey.make(s1)

    it("should allow single val") {
      val li = lk.set(tval)
      li.values should be(Array(tval))
      li.head should be(tval)
      li.get(0).get should equal(tval)
    }

    val listIn = Array[Double](123.0, 456.0)

    it("should work with list, withUnits") {
      val li = lk.set(listIn).withUnits(degree)
      li.units should be(degree)
      li.value(1) should equal(listIn(1))
      li.values should equal(listIn)
    }

    it("should work with list, units") {
      val li = lk.set(listIn, degree)
      li.units should be(degree)
      li.value(1) should equal(listIn(1))
      li.values should equal(listIn)
    }
  }

  describe("test doubleArrayKey") {
    val a1 = Array[Double](1.0, 2.0, 3.0, 4.0, 5.0)
    val a2 = Array[Double](10.0, 20.0, 30.0, 40.0, 50.0)

    val la1 = ArrayData(a1)
    val la2 = ArrayData(a2)
    val lk  = KeyType.DoubleArrayKey.make(s1)

    it("should test single item") {
      val di = lk.set(la1)
      di.values should equal(Array(la1))
      di.head should be(la1)
      di.get(0).get should equal(la1)
    }

    val listIn = Array(la1, la2)

    it("should test with list, withUnits") {
      val li2 = lk.set(listIn).withUnits(degree)
      li2.units should be(degree)
      li2.value(1) should equal(listIn(1))
      li2.values should equal(listIn)
    }

    it("should test with list, units") {
      val li2 = lk.set(listIn, degree)
      li2.units should be theSameInstanceAs degree
      li2.value(1) should equal(listIn(1))
      li2.values should equal(listIn)
    }

    it("should test using one array with and without units") {
      var li2 = lk.set(a1) // Uses implicit to create from long array
      li2.head should equal(la1)
      li2 = lk.set(a2).withUnits(degree)
      li2.units should be theSameInstanceAs degree
      li2.head should equal(la2)
    }

    it("should test using var args") {
      val li3 = lk.set(la1, la2)
      li3.value(1) should equal(la2)
      li3.values should equal(listIn)

      val a: Array[Double] = Array(1, 2, 3)
      val b: Array[Double] = Array(10, 20, 30)
      val c: Array[Double] = Array(100, 200, 300)

      val li4 = lk.set(a, b, c).withUnits(meter)
      li4.values.length should be(3)
      li4.value(2) should equal(ArrayData(c))
    }
  }

  describe("test doubleMatrixKey") {
    val m1: Array[Array[Double]] = Array(Array(1, 2, 3), Array(4, 5, 6), Array(7, 8, 9))
    val m2: Array[Array[Double]] = Array(Array(1, 2, 3, 4, 5), Array(10, 20, 30, 40, 50))

    val lm1 = MatrixData.fromArrays(m1)
    val lm2 = MatrixData.fromArrays(m2)
    val dk  = DoubleMatrixKey.make(s1)

    it("should work with a single item") {
      val di = dk.set(lm1)
      di.values should equal(Array(lm1))
      di.head should equal(lm1)
      di.get(0).get should equal(lm1)
    }

    val listIn = Array(lm1, lm2)

    it("should work with list and withUnits") {
      val di = dk.set(listIn).withUnits(degree)
      di.units should be theSameInstanceAs degree
      di.value(1) should equal(listIn(1))
      di.values should equal(listIn)
    }

    it("should work with list and units") {
      val di = dk.set(listIn, degree)
      di.units should be theSameInstanceAs degree
      di.value(1) should equal(listIn(1))
      di.values should equal(listIn)
    }

    it("work with one matrix without and with units") {
      var di = dk.set(m1) // This is an implicit
      di.head should equal(lm1)
      di = dk.set(m1).withUnits(degree)
      di.units should be theSameInstanceAs degree
      di.head should equal(lm1)
    }

    it("work with varargs") {
      val di = dk.set(lm1, lm2).withUnits(second)
      di.units should be theSameInstanceAs second
      di.value(1) should equal(lm2)
      di.values should equal(listIn)
    }

    it("work with varargs as arrays") {
      val di = dk.set(m1, m2).withUnits(meter)
      di.units should be theSameInstanceAs meter
      di.value(0) should equal(lm1)
      di.values should equal(listIn)
    }
  }

  //DEOPSCSW-282: Add a timestamp Key and Parameter
  describe("test TimestampItem") {
    val tsval: Instant      = Instant.now()
    val tskey: Key[Instant] = KeyType.TimestampKey.make(s1)

    it("should allow create a Timestamp parameter from a timestamp key") {
      val li: Parameter[Instant] = tskey.set(tsval)
      li.values should be(Array(tsval))
      li.head should be(tsval)
      li.get(0).get should equal(tsval)
    }

    it("and second must be default Unit") {
      val li1: Parameter[Instant] = tskey.set(tsval)
      li1.units should be(second)

      //must respect overriding also
      val li2: Parameter[Instant] = tskey.set(tsval).withUnits(NoUnits)
      li2.units should be(NoUnits)
    }

    val listIn = Array[Instant](Instant.now().minusSeconds(3600), Instant.now(), Instant.now().plusMillis(3600000))

    it("should work with list, withUnits") {
      val li = tskey.set(listIn).withUnits(second)
      li.units should be(second)
      li.value(0) should equal(listIn(0))
      li.value(1) should equal(listIn(1))
      li.value(2) should equal(listIn(2))
      li.values should equal(listIn)
    }

    it("should work with list, units") {
      val li: Parameter[Instant] = tskey.set(listIn, second)
      li.units should be(second)
      li.value(0) should equal(listIn(0))
      li.value(1) should equal(listIn(1))
      li.value(2) should equal(listIn(2))
      li.values should equal(listIn)
    }
  }

  describe("testing ChoiceItem") {
    it("should allow creating with Choices object") {
      // Choices as object with String input
      val choices = Choices.from("A", "B", "C")

      val ci1 = ChoiceKey.make("mode", choices)
      ci1.choices should equal(choices)
      ci1.keyName should be("mode")

      val ci = ci1.set("A")
      ci.head should equal(Choice("A"))
      // Check that non-choice fails
      an[AssertionError] should be thrownBy ci1.set("D")
    }

    it("should allow creating with varargs of Strings") {
      // Create directly with keyname, and Choice names
      val ci2 = ChoiceKey.make("test", "A", "B")
      ci2.choices should equal(Choices.from("A", "B"))
      ci2.keyName should be("test")

      val ci = ci2.set("A")
      ci.head should equal(Choice("A"))
      // Check that non-choice fails
      an[AssertionError] should be thrownBy ci2.set("C")
    }

    it("should allow creation with individual Choice items") {
      // Now create with individual Choice items
      val uninitialized = Choice("uninitialized")
      val ready         = Choice("ready")
      val busy          = Choice("busy")
      val continuous    = Choice("continuous")
      val error         = Choice("error")

      val cmd = ChoiceKey.make("cmd", uninitialized, ready, busy, continuous, error)
      cmd.choices should be(Choices(Set(uninitialized, ready, busy, continuous, error)))

      // setting values
      val ci = cmd.set(ready)
      ci.head should equal(ready)
    }
  }

  describe("test raDecKey") {

    val keyName  = "raDecKey"
    val raDec1   = RaDec(1.0, 2.0)
    val raDec2   = RaDec(3.0, 4.0)
    val raDecKey = KeyType.RaDecKey.make(keyName)

    it("should allow single val") {
      val ii = raDecKey.set(raDec1)
      ii.values should be(Array(raDec1))
      ii.get(0).get should equal(raDec1)
    }

    val listIn = Array(raDec1, raDec2)

    it("should work with list, withUnits") {
      val ii = raDecKey.set(listIn).withUnits(degree)
      ii.units should be(degree)
      ii.value(1) should equal(listIn(1))
      ii.values should equal(listIn)
    }

    it("should work with list, units") {
      val ii = raDecKey.set(listIn, degree)
      ii.units should be(degree)
      ii.value(1) should equal(listIn(1))
      ii.values should equal(listIn)
    }
  }

  describe("test stringKey") {

    val keyName   = "stringKey"
    val stringKey = KeyType.StringKey.make(keyName)

    it("should allow single val") {
      val ii = stringKey.set("First")
      ii.values should be(Array("First"))
      ii.get(0).get should equal("First")
    }

    val listIn = Array("First", "Second")

    it("should work with list, withUnits") {
      val ii = stringKey.set(listIn).withUnits(degree)
      ii.units should be(degree)
      ii.value(1) should equal(listIn(1))
      ii.values should equal(listIn)
    }

    it("should work with list, units") {
      val ii = stringKey.set(listIn, degree)
      ii.units should be(degree)
      ii.value(1) should equal(listIn(1))
      ii.values should equal(listIn)
    }
  }

  describe("testing StructItem") {
    it("should allow creating Struct items") {
      val skey = StructKey.make("myStruct")

      val ra    = KeyType.StringKey.make("ra")
      val dec   = KeyType.StringKey.make("dec")
      val epoch = KeyType.DoubleKey.make("epoch")
      val sc1   = Struct().madd(ra.set("12:13:14.1"), dec.set("32:33:34.4"), epoch.set(1950.0))

      val citem = skey.set(sc1)

      assert(citem.size == 1)
      assert(citem.head.size == 3)
      assert(citem.head.get(ra).head.head == "12:13:14.1")
      assert(citem.head.get(dec).head.head == "32:33:34.4")
      assert(citem.head.get(epoch).head.head == 1950.0)

    }
  }

  // DEOPSCSW-190: Implement Unit Support
  // DEOPSCSW-184: Change configurations - attributes and values
  describe("test setting multiple values") {

    val intKey = KeyType.IntKey.make("test1")
    it("should allow setting a single value") {
      val i1 = intKey.set(1)
      assert(i1.values === Array(1))
      assert(i1.units == NoUnits)
      assert(i1(0) == 1)
    }
    it("should allow setting several") {
      val i1 = intKey.set(1, 3, 5, 7)
      assert(i1.values === Array(1, 3, 5, 7))
      assert(i1.units == NoUnits)
      assert(i1(1) == 3)

      val i2 = intKey.set(Array(10, 30, 50, 70)).withUnits(degree)
      assert(i2.values === Array(10, 30, 50, 70))
      assert(i2.units == degree)
      assert(i2(1) == 30)
      assert(i2(3) == 70)

      // to prove that existing Parameter is not mutated and every time `set` is called on key, it creates new Parameter
      assert(i1.values === Array(1, 3, 5, 7))
    }
    it("should also allow setting with sequence") {
      val s1 = Array(2, 4, 6, 8)
      val i1 = intKey.set(s1).withUnits(meter)
      assert(i1.values === s1)
      assert(i1.values.length == s1.length)
      assert(i1.units == meter)
      assert(i1(2) == 6)
    }
  }
}
