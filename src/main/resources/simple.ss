import akka.stream.scaladsl.Source
import example.LocationService
import example.Script
import example.Factory

class SimpleScript(ls: LocationService) extends Script {
  def run() = {
    println(Source(1 to 10))
    println("abc")
    println(ls.m)
  }
}

class ScriptFactory extends Factory {
    def create(): Script = {
        val ls = new LocationService
        new SimpleScript(ls)
    }
}

scala.reflect.classTag[ScriptFactory].runtimeClass
