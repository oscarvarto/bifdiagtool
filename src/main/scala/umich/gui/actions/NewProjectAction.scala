package umich.gui.actions

import java.awt.event.ActionEvent
import javax.swing.AbstractAction

import umich.gui.{
  NewProjectFrame,
  NewDynamicalSystemFrame,
  ConfParamFrame
}

import umich.parser.DynSys
import umich.simulation.Project

class NewProjectAction() extends AbstractAction("New Project") {
  var cont: (Unit ⇒ Unit) = null
  import util.continuations._

  def actionPerformed(event: ActionEvent) = reset {
    val projName = getProjectName()
    val dynSys = getDynamicalSystem()
    val proj1 = addParamConfig(Project(projName, dynSys))
    val proj2 = if (dynSys.maybeTwoParameterSimulation)
                addParamConfig(proj1) else proj1
    processProject(proj2)
  }

  import scalaz.syntax.std.option._

  def getProjectName(): String @cps[Unit] = {
    val npf = new NewProjectFrame()
    npf.pack()
    npf.setVisible(true)
    shift {
      k: (Unit ⇒ Unit) ⇒
        {
          cont = k
          npf.cont = Some(cont)
        }
    }
    npf.setVisible(false)
    npf.getProjectName().err("If validation was Ok, this should never happen")
  }

  def getDynamicalSystem(): DynSys @cps[Unit] = {
    import umich.parser.Parser
    import scalaz.syntax.validation._
    import scalaz.{ ValidationNel, NonEmptyList }
    val parsingFunc: String ⇒ ValidationNel[String, DynSys] = { s ⇒
      val res = Parser.parse(s)
      if (res.successful) res.get.successNel else NonEmptyList("Please check your syntax").failure
    }
    val nDynSysF = new NewDynamicalSystemFrame(None, parsingFunc)
    nDynSysF.pack()
    nDynSysF.setVisible(true)
    shift {
      k: (Unit ⇒ Unit) ⇒
        {
          cont = k
          nDynSysF.cont = Some(cont)
        }
    }
    nDynSysF.setVisible(false)
    nDynSysF.getDynSys().err("If validation was Ok, this should never happen")
  }

  import umich.parser.Names._
  import umich.simulation.ParameterConfig
  def addParamConfig(proj: Project): Project @cps[Unit] = {
    val cpf = new ConfParamFrame(proj)
    cpf.pack()
    cpf.setVisible(true)
    shift {
      k: (Unit ⇒ Unit) ⇒
        {
          cont = k
          cpf.cont = Some(cont)
        }
    }
    cpf.setVisible(false)
    import scalaz.syntax.std.option._
    cpf.getParamConfig().cata(
      t ⇒ proj.addParamConf(t),
      proj)
  }

  def processProject(proj: Project) {
    println(proj)
  }
}
