import tmt.sequencer.ScriptImports._

while (true) {
  val command = E.pullNext()

  if (command < 2) {
    println((command, "double", Sync.double(command)))
  }
  else if (command < 4) {
    println((command, "square", Sync.square(command)))
  }
  else {
    val results = Async.par(Async.double(command - 4), Async.square(4))
    println((command, "sum", results.sum))
  }
}
