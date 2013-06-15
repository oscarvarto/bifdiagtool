package umich.simulation

import collection.immutable.TreeMap

import umich.parser.Names._
import umich.parser.DynSysSaddleNode.dynSys

object saddleNodeProject extends Project("Saddle Node",
  dynSys,
  TreeMap(ParamName("_r") → ParameterConfig(-5.0, 0.0, 0.1).toOption.get))
