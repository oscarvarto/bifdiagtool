package umich.simulation

import collection.immutable.TreeMap

import umich.parser.Names._
import umich.parser.DynSysSpring.dynSys

object dampedUndrivenHookeLawSpringProject extends Project("DampedUndrivenHookeLawSpring",
  dynSys,
  TreeMap(
    ParamName("_w0") → ParameterConfig(1.0, 5.0, 1.0).toOption.get,
    ParamName("_c") → ParameterConfig(1.0, 5.0, 1.0).toOption.get))
