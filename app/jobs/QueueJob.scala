package jobs

import javax.inject.{Inject, Named, Singleton}

import akka.actor.{Actor, ActorRef, ActorSystem}
import controllers.QueueController
import model.OligoJob

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration


@Singleton
class SchedulerActor @Inject() (queueController: QueueController) extends Actor {
  def receive = {
    case "runQueue" => poll()
  }

  def poll(): Unit = {
    if (queueController.peek.isEmpty) {
      // wait
    } else {
      val job: OligoJob = queueController.next.get

    }
  }
}

class Scheduler @Inject() (val system: ActorSystem, @Named("scheduler-actor") val schedulerActor: ActorRef)(implicit ec: ExecutionContext) {
  system.scheduler.schedule(
    FiniteDuration(0, "milliseconds"), FiniteDuration(5, "seconds"), schedulerActor, "runQueue"
  )
}