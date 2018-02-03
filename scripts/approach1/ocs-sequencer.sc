import tmt.approach1.ScriptImports._

while (true) {
  val command = E.pullNext()

  if (command < 2) {
    println((command, "double", D.double(command)))
  }
  else if (command < 4) {
    println((command, "square", D.square(command)))
  }
  else {
    println((command, "sum", D.sum(D.doubleAsync(command - 4), D.squareAsync(4))))
  }
}
