package scala.compat.java8

object OptionConverters {
  implicit class RichOptionForJava8[A](val underlying: Option[A]) extends AnyVal {
    def asJava: java.util.Optional[A] = ???
  }

  implicit class RichOptionalGeneric[A](val underlying: java.util.Optional[A]) {
    def asScala: Option[A] = ???
  }
}
