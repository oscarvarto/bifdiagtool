package umich.fixedPoints.pso

import net.sourceforge.cilib.algorithm.initialisation.{ ClonedPopulationInitialisationStrategy, PopulationInitialisationStrategy }
import net.sourceforge.cilib.algorithm.population.IterationStrategy
import net.sourceforge.cilib.controlparameter.ConstantControlParameter

import net.sourceforge.cilib.niching.NichingAlgorithm
import net.sourceforge.cilib.niching.creation.{ ClosestNeighbourNicheCreationStrategy, MaintainedFitnessNicheDetection }
import net.sourceforge.cilib.niching.iterationstrategies.NichePSO
import net.sourceforge.cilib.niching.iterators.{ AllSwarmsIterator, SingleNicheIteration, SubswarmIterator }
import net.sourceforge.cilib.niching.merging.{ SingleSwarmMergeStrategy, StandardMergeStrategy }
import net.sourceforge.cilib.niching.merging.detection.{ DiversityBasedMergeDetection, RadiusOverlapMergeDetection }
import net.sourceforge.cilib.problem.boundaryconstraint.ReinitialisationBoundary
import net.sourceforge.cilib.pso.PSO
import net.sourceforge.cilib.pso.iterationstrategies.SynchronousIterationStrategy

import net.sourceforge.cilib.pso.particle.{ Particle, ParticleBehavior, StandardParticle }
import net.sourceforge.cilib.pso.dynamic.{ ChargedParticle, ChargedVelocityProvider, DynamicParticle, DynamicIterationStrategy }
import net.sourceforge.cilib.entity.topologies.VonNeumannNeighbourhood
import net.sourceforge.cilib.algorithm.initialisation.ChargedPopulationInitialisationStrategy
import net.sourceforge.cilib.pso.dynamic.detectionstrategies.StoppingConditionDetectionStrategy
import net.sourceforge.cilib.stoppingcondition.OptimiserStalled
import net.sourceforge.cilib.pso.dynamic.responsestrategies.PartialReinitialisationResponseStrategy

import net.sourceforge.cilib.pso.velocityprovider.{ ClampingVelocityProvider, ConstrictionVelocityProvider, GCVelocityProvider, StandardVelocityProvider }

import umich.simulation.Project

/**
  * This class creates a Niche PSO algorithm with numPart particles
  */
class NPSO(val numPart: Int) extends NichingAlgorithm {
  this.mainSwarm = new PSO()

  // Initialisation Strategy
  val velProv = new StandardVelocityProvider()
  velProv.setSocialAcceleration(ConstantControlParameter.of(0.0))
  velProv.setCognitiveAcceleration(ConstantControlParameter.of(1.2))

  val particle = new StandardParticle()
  particle.setVelocityProvider(velProv)
  //getVelocityProvider().asInstanceOf[ChargedVelocityProvider].setDelegate(velProv)

  val initStrat = new ClonedPopulationInitialisationStrategy()
  initStrat.setEntityType(particle)
  initStrat.setEntityNumber(numPart)
  this.mainSwarm.setInitialisationStrategy(initStrat)

  // Iteration Strategy

  val iterStrat = new SynchronousIterationStrategy()
  iterStrat.setBoundaryConstraint(new ReinitialisationBoundary())
  this.mainSwarm.asInstanceOf[PSO].setIterationStrategy(iterStrat)

  // Subswarm type and behavior
  val psoSubSwarm = new PSO()
  psoSubSwarm.setIterationStrategy(iterStrat)
  import net.sourceforge.cilib.entity.topologies.LBestNeighbourhood
  psoSubSwarm.setNeighbourhood(new LBestNeighbourhood())
  // nicheCreator swarmType
  this.nicheCreator = new ClosestNeighbourNicheCreationStrategy()
  this.nicheCreator.setSwarmType(psoSubSwarm)

  // velocityProvider
  val subSwarmVelProv = new GCVelocityProvider()
  subSwarmVelProv.setRho(ConstantControlParameter.of(1.0))
  // epsilons y epsilonf
  subSwarmVelProv.setSuccessCountThreshold(15)
  subSwarmVelProv.setFailureCountThreshold(5)
  val constrictionVelProv = new ConstrictionVelocityProvider()
  constrictionVelProv.setSocialAcceleration(ConstantControlParameter.of(2.05))
  constrictionVelProv.setCognitiveAcceleration(ConstantControlParameter.of(2.05))
  constrictionVelProv.setKappa(ConstantControlParameter.of(1.0))
  val clampingVelProv = new ClampingVelocityProvider(ConstantControlParameter.of(1.0),
    constrictionVelProv)
  subSwarmVelProv.setDelegate(clampingVelProv)

  val subSwarmBehavior = new ParticleBehavior()
  subSwarmBehavior.setVelocityProvider(subSwarmVelProv)
  this.nicheCreator.setSwarmBehavior(subSwarmBehavior)

  // Niche Detector
  this.nicheDetector = new MaintainedFitnessNicheDetection()
  this.nicheDetector.asInstanceOf[MaintainedFitnessNicheDetection].
    setThreshold(ConstantControlParameter.of(1.0E-12))
  this.nicheDetector.asInstanceOf[MaintainedFitnessNicheDetection].
    setStationaryCounter(ConstantControlParameter.of(3.0))

  // Merge Detector
  import net.sourceforge.cilib.niching.merging.detection.DiversityBasedMergeDetection
  this.mergeDetector = new DiversityBasedMergeDetection()
  this.mergeDetector.asInstanceOf[DiversityBasedMergeDetection]
    .setThreshold(ConstantControlParameter.of(1.0e-12))

  // this.mergeDetector = new RadiusOverlapMergeDetection()
  // this.mergeDetector.asInstanceOf[RadiusOverlapMergeDetection]
  //   .setThreshold(ConstantControlParameter.of(1.0E-20))

  //DiversityBasedMergeDetection()

  // Absortion Detector
  this.absorptionDetector = new RadiusOverlapMergeDetection()
  this.absorptionDetector.asInstanceOf[RadiusOverlapMergeDetection].
    setThreshold(ConstantControlParameter.of(1.0E-20))

  import net.sourceforge.cilib.niching.merging.ScatterMergeStrategy

  this.mainSwarmMerger = new SingleSwarmMergeStrategy()

  this.mainSwarmAbsorber = new SingleSwarmMergeStrategy()
  this.mainSwarmCreationMerger = new SingleSwarmMergeStrategy()

  this.subSwarmMerger = new StandardMergeStrategy()

  this.subSwarmAbsorber = new StandardMergeStrategy()

  this.subSwarmIterator = new AllSwarmsIterator()
  this.subSwarmIterator.asInstanceOf[SubswarmIterator].
    setIterator(new SingleNicheIteration())

  this.iterationStrategy = new NichePSO()
}
