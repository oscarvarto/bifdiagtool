package umich.simulation

import collection.immutable.TreeMap

import umich.parser.Names._
import umich.parser.DynSysSubcriticalPitchforkBif.dynSys

object subcriticalPitchforkBifProject extends Project("Subcritical Pitchfork Bifurcation",
  dynSys,
  TreeMap(ParamName("_r") → ParameterConfig(-0.3, 0.25, 0.05).toOption.get))
