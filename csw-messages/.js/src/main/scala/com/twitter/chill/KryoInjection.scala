package com.twitter.chill

import scala.util.Try

object KryoInjection {
  def apply(obj: Any): Array[Byte]        = ???
  def invert(b: Array[Byte]): Try[AnyRef] = ???

}
