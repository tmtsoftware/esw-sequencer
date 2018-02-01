import akka.stream.scaladsl.Source
import example.LocationService
import example.Script
import example.ScriptFactory

class SimpleScript(ls: LocationService) extends Script {
  def run() = {
    <script>
  }
}

class SimpleScriptFactory extends ScriptFactory {
    def build(): Script = {
        val ls = new LocationService
        new SimpleScript(ls)
    }
}

scala.reflect.classTag[SimpleScriptFactory].runtimeClass
