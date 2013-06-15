package umich.gui

import scala.collection.immutable.Vector

import umich.simulation.Project

case class WorkspaceImpl(
  name: String,
  projects: Vector[Project] = Vector.empty) extends Workspace {

  import WorkspaceImpl._

  def addProject(project: Project): Workspace = {
    val projs = projectsLens.get(this) :+ project // append project
    projectsLens.set(this)(projs)
  }

  def removeProject(project: Project): Workspace = {
    val projs = projectsLens.get(this) filterNot (p ⇒ p equals project)
    projectsLens.set(this)(projs)
  }

  def open(): Workspace = this

  def close(): Unit = {}
}

object WorkspaceImpl {
  import shapeless._
  import shapeless.Nat._

  implicit val workspaceIso = Iso.hlist(WorkspaceImpl.apply _, WorkspaceImpl.unapply _)
  val projectsLens = Lens[WorkspaceImpl] >> _1
}
