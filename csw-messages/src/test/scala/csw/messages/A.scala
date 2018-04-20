package csw.messages
import csw.messages.commands.Keys
import csw.messages.params.generics.Key

import scala.scalajs.js.annotation.{JSExportAll, JSExportTopLevel}

@JSExportTopLevel("A")
@JSExportAll
object A {
  def dd(): Key[String] = Keys.CancelKey
}
