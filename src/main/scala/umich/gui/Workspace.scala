package umich.gui

import umich.simulation.Project

trait Workspace {
  def name: String
  def projects: IndexedSeq[Project]
  def addProject(project: Project): Workspace
  def removeProject(project: Project): Workspace
  def open(): Workspace
  def close(): Unit
}
