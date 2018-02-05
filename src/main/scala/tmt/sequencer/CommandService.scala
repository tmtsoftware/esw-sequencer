package tmt.sequencer

import tmt.services.LocationService

class CommandService(locationService: LocationService) {
  def square(x: Int): Int = x * x
  def double(x: Int): Int = x + x

  def dummy(): Unit = println(s"location service says ${locationService.m}")
}
