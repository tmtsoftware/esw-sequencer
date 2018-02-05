import tmt.sequencer.ScriptImports._

forEach { command =>
  if (command < 2) {
    println((command, "double", D.double(command)))
  }
  else if (command < 4) {
    println((command, "square", D.square(command)))
  }
  else {
    val results = D.par(D.double(command - 4), D.square(4))
    println((command, "sum", results.sum))
  }
}
