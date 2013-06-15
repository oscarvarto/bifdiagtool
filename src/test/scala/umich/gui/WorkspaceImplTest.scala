package umich.gui

import org.scalatest.FunSuite
import org.scalatest.GivenWhenThen
import org.scalatest.Matchers
//import tagobjects.GUITest

class WorkspaceImplTest extends FunSuite with Matchers with GivenWhenThen {
  import umich.parser.{ DynSys1, DynSys2 }

  test("WorkspaceImpl can add and remove projects") {
    Given("An empty workspace and two dummy projects")
    val dummyWorkspace = WorkspaceImpl("DummyWorkspace")
    import umich.simulation.Project
    val proj1 = Project("Proj1", DynSys1.dynSys)
    val proj2 = Project("Proj2", DynSys2.dynSys)

    When("Dummy projects are added")
    val modifiedWorkspace = dummyWorkspace.addProject(proj1).addProject(proj2)

    Then("The number of projects in the workspace should be two")
    modifiedWorkspace.projects.size should be(2)

    Then("If the first project of the workspace is removed, the number of projects should be one less")
    val finalWorkspace = modifiedWorkspace.removeProject(proj1)
    finalWorkspace.projects.size should be(1)

    And("The remaining project should be the second one")
    finalWorkspace.projects(0) should be(proj2)
  }

}
