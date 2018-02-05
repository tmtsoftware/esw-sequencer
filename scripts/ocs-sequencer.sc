import tmt.sequencer.Dsl._

forEach { command =>
  if (command < 2) {
    println((command, "double", CS.double(command)))
  }
  else if (command < 4) {
    println((command, "square", CS.square(command)))
  }
  else {
    val results = par(CS.double(command - 4), CS.square(4))
    println((command, "sum", results.sum))
  }
}
