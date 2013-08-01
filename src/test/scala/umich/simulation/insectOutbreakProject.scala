package umich.simulation

import collection.immutable.TreeMap

import umich.parser.Names._
import umich.parser.DynSysInsectOutbreak.dynSys

object insectOutbreakProject extends Project("Insect Outbreak",
  dynSys,
  TreeMap(ParamName("_r") → ParameterConfig(0.0, 30.0, 10.0).toOption.get,
    ParamName("_k") → ParameterConfig(0.05, 0.5, 0.05).toOption.get))
