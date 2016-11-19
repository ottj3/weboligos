package controllers

import javax.inject.Singleton

import model.OligoJob
import play.api.mvc._

import scala.collection.mutable

@Singleton
class QueueController extends Controller {

  val queue = mutable.Queue[OligoJob]()

  def add(job: OligoJob) {
    queue += job
  }

  def next: Option[OligoJob] = {
    if(queue.nonEmpty)
      Some(queue.dequeue())
    else None
  }

  def peek: Option[OligoJob] = {
    queue.headOption
  }
}
