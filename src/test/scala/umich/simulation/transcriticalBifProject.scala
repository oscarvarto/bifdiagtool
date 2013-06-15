package umich.simulation

import collection.immutable.TreeMap

import umich.parser.Names._
import umich.parser.DynSysTranscriticalBif.dynSys

object transcriticalBifProject extends Project("Transcritical Bifurcation",
  dynSys,
  TreeMap(ParamName("_r") → ParameterConfig(-5.0, 5.0, 0.1).toOption.get))
