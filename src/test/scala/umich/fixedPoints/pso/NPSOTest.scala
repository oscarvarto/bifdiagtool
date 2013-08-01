package umich.fixedPoints.pso

import org.scalatest.{ FunSuite, Matchers }

import umich.simulation.{ ParameterConfig, saddleNodeProject, subcriticalPitchforkBifProject }

class SaddleNodeNPSOTest extends FunSuite {
  test("saddleNode experiment") {
    val dynSys = saddleNodeProject.dynSys
    // val parConf: ParameterConfig = ParameterConfig(-5.0, 0.0, 2.5).toOption.get
    // //saddleNodeProject.paramConfs.values.head

    // val (from, to, step) = (parConf.from, parConf.to, parConf.step)
    // val n = ((to - from) / step + 1).floor.toInt
    // val parValues = Array.tabulate(n)(_ * step + from)
    val numParticles = 16
    val domain = "R(-5.0:5.0)"

    import net.sourceforge.cilib.niching.NichingAlgorithm
    //import net.sourceforge.cilib.niching.SequentialNichingTechnique

    val npso = new NPSO(numParticles)
    import net.sourceforge.cilib.problem.FunctionOptimisationProblem
    //import net.sourceforge.cilib.problem.DeratingOptimisationProblem
    val problem = new FunctionOptimisationProblem()
    problem.setDomain(domain)
    import net.sourceforge.cilib.problem.objective.Minimise // Maximise
    problem.setObjective(new Minimise())
    //new Maximise())

    import fj.F
    import net.sourceforge.cilib.`type`.types.container.{ Vector ⇒ CilibVector }
    import java.lang.{ Double ⇒ JDouble }
    val fn = new F[CilibVector, JDouble]() {
      import umich.simulation.OneVariableFunctionEvaluator
      val fEv = OneVariableFunctionEvaluator(saddleNodeProject)
      import umich.parser.Names._
      val closedParams = Map(ParamName("_r") → -0.1)
      def f(input: CilibVector): JDouble = {
        import spire.implicits._
        (fEv.f(closedParams)(input.get(0).doubleValue)).abs
      }
    }
    problem.setFunction(fn)
    npso.setOptimisationProblem(problem)
    import net.sourceforge.cilib.stoppingcondition.MeasuredStoppingCondition
    import net.sourceforge.cilib.measurement.generic.Iterations
    import net.sourceforge.cilib.stoppingcondition.Maximum
    npso.addStoppingCondition(new MeasuredStoppingCondition(
      new Iterations(),
      new Maximum(),
      200))

    npso.performInitialisation()
    npso.run()
    import collection.JavaConversions._
    val sols = for {
      single ← npso.getPopulations()
    } yield single.getBestSolution().getPosition()
    sols.foreach { println }
  }
}

class SubcriticalPitchforkNPSOTest extends FunSuite {
  test("Canonical Subcritical Pitchfork Bifurcation problem") {
    val dynSys = subcriticalPitchforkBifProject.dynSys
    val numParticles = 100
    val domain = "R(-1.2:1.2)"

    import net.sourceforge.cilib.niching.NichingAlgorithm
    import net.sourceforge.cilib.problem.FunctionOptimisationProblem
    import net.sourceforge.cilib.stoppingcondition.MeasuredStoppingCondition
    import net.sourceforge.cilib.measurement.generic.Iterations
    import net.sourceforge.cilib.stoppingcondition.Maximum
    import fj.F
    import net.sourceforge.cilib.`type`.types.container.{ Vector ⇒ CilibVector }
    import java.lang.{ Double ⇒ JDouble }

    //import net.sourceforge.cilib.niching.SequentialNichingTechnique
    def oneStep(r: Double): Seq[(Double, Double)] = {
      val npso = new NPSO(numParticles)

      //import net.sourceforge.cilib.problem.DeratingOptimisationProblem
      val problem = new FunctionOptimisationProblem()
      problem.setDomain(domain)
      import net.sourceforge.cilib.problem.objective.Minimise // Maximise
      problem.setObjective(new Minimise())
      //new Maximise())
      npso.addStoppingCondition(new MeasuredStoppingCondition(
        new Iterations(),
        new Maximum(),
        200))

      val fn = new F[CilibVector, JDouble]() {
        import umich.simulation.OneVariableFunctionEvaluator
        val fEv = OneVariableFunctionEvaluator(subcriticalPitchforkBifProject)
        import umich.parser.Names._
        val closedParams = Map(ParamName("_r") → r)
        def f(input: CilibVector): JDouble = {
          import spire.implicits._
          (fEv.f(closedParams)(input.get(0).doubleValue)).abs
        }
      }
      problem.setFunction(fn)
      npso.setOptimisationProblem(problem)

      npso.performInitialisation()
      npso.run()
      import collection.JavaConversions._
      import net.sourceforge.cilib.`type`.types.Real
      val sols: Seq[Double] = for {
        single ← npso.getPopulations()
        sol = single.getBestSolution().getPosition().asInstanceOf[CilibVector]
        if fn.f(sol) < 1.0e-8
      } yield sol.doubleValueOf(0)
      sols.map { sol ⇒ (r, sol) }
    }

    val rs = Array.tabulate(56) { -0.30 + 0.01 * _ }
    val fp = for {
      r ← rs
    } yield oneStep(r)
    val fixedPoints = fp.flatten
    import umich.plot.plotFixedPoints
    val chart = plotFixedPoints.plot2d(subcriticalPitchforkBifProject, fixedPoints, "")
    chart.show()
  }
}

// class HopfNPSOTest extends FunSuite {

// todo: use section 2.8 of Seydel (Lorentz Equations with Hopf bifurcation) for this test

//   test("Canonical Subcritical Pitchfork Bifurcation problem") {
//     val dynSys = subcriticalPitchforkBifProject.dynSys
//     val numParticles = 100
//     val domain = "R(-1.2:1.2)"

//     import net.sourceforge.cilib.niching.NichingAlgorithm
//     import net.sourceforge.cilib.problem.FunctionOptimisationProblem
//     import net.sourceforge.cilib.stoppingcondition.MeasuredStoppingCondition
//     import net.sourceforge.cilib.measurement.generic.Iterations
//     import net.sourceforge.cilib.stoppingcondition.Maximum
//     import fj.F
//     import net.sourceforge.cilib.`type`.types.container.{ Vector ⇒ CilibVector }
//     import java.lang.{ Double ⇒ JDouble }

//     //import net.sourceforge.cilib.niching.SequentialNichingTechnique
//     def oneStep(r: Double): Seq[(Double, Double)] = {
//       val npso = new NPSO(numParticles)

//       //import net.sourceforge.cilib.problem.DeratingOptimisationProblem
//       val problem = new FunctionOptimisationProblem()
//       problem.setDomain(domain)
//       import net.sourceforge.cilib.problem.objective.Minimise // Maximise
//       problem.setObjective(new Minimise())
//       //new Maximise())
//       npso.addStoppingCondition(new MeasuredStoppingCondition(
//         new Iterations(),
//         new Maximum(),
//         200))

//       val fn = new F[CilibVector, JDouble]() {
//         import umich.simulation.OneVariableFunctionEvaluator
//         val fEv = OneVariableFunctionEvaluator(subcriticalPitchforkBifProject)
//         import umich.parser.Names._
//         val closedParams = Map(ParamName("_r") → r)
//         def f(input: CilibVector): JDouble = {
//           import spire.implicits._
//           (fEv.f(closedParams)(input.get(0).doubleValue)).abs
//         }
//       }
//       problem.setFunction(fn)
//       npso.setOptimisationProblem(problem)

//       npso.performInitialisation()
//       npso.run()
//       import collection.JavaConversions._
//       import net.sourceforge.cilib.`type`.types.Real
//       val sols: Seq[Double] = for {
//         single ← npso.getPopulations()
//         sol = single.getBestSolution().getPosition().asInstanceOf[CilibVector]
//         if fn.f(sol) < 1.0e-8
//       } yield sol.doubleValueOf(0)
//       sols.map { sol ⇒ (r, sol) }
//     }

//     val rs = Array.tabulate(56) { -0.30 + 0.01 * _ }
//     val fp = for {
//       r ← rs
//     } yield oneStep(r)
//     val fixedPoints = fp.flatten
//     import umich.plot.plotFixedPoints
//     val chart = plotFixedPoints.plot2d(subcriticalPitchforkBifProject, fixedPoints, "Hello")
//     chart.show()
//   }
// }

class InsectOutbreakNPSOTest extends FunSuite {
  import umich.simulation.insectOutbreakProject

  test("Insect Outbream problem") {
    val dynSys = insectOutbreakProject.dynSys
    val numParticles = 50
    val domain = "R(0.1:40.0)"

    import net.sourceforge.cilib.niching.NichingAlgorithm
    import net.sourceforge.cilib.problem.FunctionOptimisationProblem
    import net.sourceforge.cilib.stoppingcondition.MeasuredStoppingCondition
    import net.sourceforge.cilib.measurement.generic.Iterations
    import net.sourceforge.cilib.stoppingcondition.Maximum
    import fj.F
    import net.sourceforge.cilib.`type`.types.container.{ Vector ⇒ CilibVector }
    import java.lang.{ Double ⇒ JDouble }

    def oneStep(r: Double, k: Double): Seq[Array[Double]] = {
      val npso = new NPSO(numParticles)

      val problem = new FunctionOptimisationProblem()
      problem.setDomain(domain)
      import net.sourceforge.cilib.problem.objective.Minimise
      problem.setObjective(new Minimise())
      npso.addStoppingCondition(new MeasuredStoppingCondition(
        new Iterations(),
        new Maximum(),
        200))

      val fn = new F[CilibVector, JDouble]() {
        import umich.simulation.OneVariableFunctionEvaluator
        val fEv = OneVariableFunctionEvaluator(insectOutbreakProject)
        import umich.parser.Names._
        val closedParams = Map(ParamName("_r") → r, ParamName("_k") → k)
        def f(input: CilibVector): JDouble = {
          import spire.implicits._
          (fEv.f(closedParams)(input.get(0).doubleValue)).abs
        }
      }
      problem.setFunction(fn)
      npso.setOptimisationProblem(problem)

      npso.performInitialisation()
      npso.run()
      import collection.JavaConversions._
      import net.sourceforge.cilib.`type`.types.Real
      val sols: Seq[Double] = for {
        single ← npso.getPopulations()
        sol = single.getBestSolution().getPosition().asInstanceOf[CilibVector]
        if fn.f(sol) < 1.0e-8
      } yield sol.doubleValueOf(0)
      sols.map { sol ⇒ Array(k, r, sol) }
    }

    val rs = Array.tabulate(121) { 0.1 + 0.005 * _ }
    val ks = Array.tabulate(8) { 5.0 + 5.0 * _ }
    val fp = for {
      r ← rs
      k ← ks
    } yield oneStep(r, k)
    val fixedPoints = fp.flatten
    import umich.plot.plotFixedPoints
    plotFixedPoints.plot3d(insectOutbreakProject, fixedPoints)
  }
}
