package jobs

import javax.inject.{Inject, Named, Singleton}

import akka.actor.{Actor, ActorRef, ActorSystem}
import controllers.QueueController
import edu.tcnj.oligos.library.Library.Phase
import edu.tcnj.oligos.library.OutOfSwapsException
import edu.tcnj.oligos.ui.Runner
import model.{OligoJob, ResultLibrary}
import services.JavaHelper

import scala.concurrent.ExecutionContext
import scala.concurrent.duration.FiniteDuration


@Singleton
class SchedulerActor @Inject() (queueController: QueueController) extends Actor {
  def receive = {
    case "runQueue" => poll()
    case "watchDog" => checkTasks()
  }

  var lastStart: Long = 0
  var currJob: OligoJob = null

  def poll(): Unit = {
    if (queueController.peek.isEmpty) {
      // noop
    } else {
      val job: OligoJob = queueController.next.get
      if (job.runner == null) {
        // new job, create and run
        val codons = JavaHelper.nonNullCodons(job.codon0.orNull, job.codon1.orNull, job.codon2.orNull, job.codon3.orNull)

        job.runner = new Runner("design.py", job.rna, job.start, job.end, job.offset, job.oligoLength, job.overlapSize,
          JavaHelper.codonNames(codons), JavaHelper.codonMins(codons),
          JavaHelper.codonMaxs(codons), JavaHelper.codonLevels(codons),
          JavaHelper.restrictions(job.restrictions),
          job.overlapDiffs
        )
        try {
          lastStart = System.currentTimeMillis()
          currJob = job
          System.out.println("RUNNER: Starting library processing on thread " + Thread.currentThread().getName)
          job.runner.run()
          job.results = new ResultLibrary(job.runner.getLastLib)
        } catch {
          case e: Throwable => handleError(e, job)
        }
        lastStart = 0
        currJob = null
      }
    }
  }

  /**
   * Set message and phase on a job that errors.
   */
  def handleError(e: Throwable, job: OligoJob): Unit = {
    e match {
      case ie: InterruptedException =>
        job.runner.getLastLib.setExecutionPhase(Phase.CANCELLED)
        job.msg = "Job was cancelled. Took too long?"
      case oose: OutOfSwapsException =>
        job.runner.getLastLib.setExecutionPhase(Phase.ERRORED)
        job.msg = "Ran out of swaps to make. Either rerun or modify your design."
      case ioobe: IndexOutOfBoundsException =>
        job.runner.getLastLib.setExecutionPhase(Phase.ERRORED)
        job.msg = "Got an invalid design. Modify your design parameters."
      case npe: NullPointerException =>
        job.runner.getLastLib.setExecutionPhase(Phase.ERRORED)
        job.msg = "Couldn't find a way to make this library without violating restrictions. Try modifying parameters."
      case rte: RuntimeException =>
        if (rte.getCause != null) handleError(rte.getCause, job)
      case t: Throwable =>
        job.runner.getLastLib.setExecutionPhase(Phase.ERRORED)
        job.msg = "Unknown error occured."
    }
  }
  val TIMEOUT_TIME = 60000 // 60 seconds

  def checkTasks(): Unit = {
    if (lastStart != 0 && (System.currentTimeMillis() - lastStart > TIMEOUT_TIME)) {
      // TODO interrupts
    }
  }
}

class Scheduler @Inject() (val system: ActorSystem, @Named("scheduler-actor") val schedulerActor: ActorRef)(implicit ec: ExecutionContext) {
  system.scheduler.schedule(
    FiniteDuration(0, "milliseconds"), FiniteDuration(5, "seconds"), schedulerActor, "runQueue"
  )
}