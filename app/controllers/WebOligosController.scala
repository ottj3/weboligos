package controllers

import javax.inject._

import com.avaje.ebean.annotation.Transactional
import edu.tcnj.oligos.library.Library
import model.Codon
import model.OligoJob
import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.mvc._
import play.api.data.validation.Constraints._
import play.api.i18n.{I18nSupport, MessagesApi}
import services.{Counter, DBQueries}



/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 * It also takes POST requests from form submissions to handle user
 * input.
 */
@Singleton
class WebOligosController @Inject() (val messagesApi: MessagesApi, val counter: Counter, val db: DBQueries, val queueController: QueueController) extends Controller with I18nSupport {

  val jobForm = Form(single("jobId" -> nonEmptyText(minLength = 6, maxLength = 6)))

  val submitForm = Form(tuple(
    "rna" -> nonEmptyText(),
    "oligoLength" -> number(min = 1),
    "overlapSize" -> number(min = 1),
    "overlapDiffs" -> number(min = 1),
    "start" -> number(),
    "end" -> number(min = 0),
    "offset" -> number(),
    "codon[0]" -> optional(
      mapping(
        "name" -> text(minLength = 3, maxLength = 3),
        "min" -> of[Double],
        "max" -> of[Double],
        "levels" -> number(min = 1)
      )(Codon.apply)(Codon.unapply)),
    "codon[1]" -> optional(
      mapping(
        "name" -> text(minLength = 3, maxLength = 3),
        "min" -> of[Double],
        "max" -> of[Double],
        "levels" -> number(min = 1)
      )(Codon.apply)(Codon.unapply)),
    "codon[2]" -> optional(
      mapping(
        "name" -> text(minLength = 3, maxLength = 3),
        "min" -> of[Double],
        "max" -> of[Double],
        "levels" -> number(min = 1)
      )(Codon.apply)(Codon.unapply)),
    "codon[3]" -> optional(
      mapping(
        "name" -> text(minLength = 3, maxLength = 3),
        "min" -> of[Double],
        "max" -> of[Double],
        "levels" -> number(min = 1)
      )(Codon.apply)(Codon.unapply)),
    "restrictions" -> optional(text verifying pattern("""([ACGTRYSWKMBDHVN]+,?)*""".r,
        error="Not a valid nucleotide sequence. Only IUPAC symbols accepted.")
    )
  ))

  /**
    * Create an Action to render an HTML page with a welcome message.
    * The configuration in the `routes` file means that this method
    * will be called when the application receives a `GET` request with
    * a path of `/`.
    */
  def index = Action {
    implicit request => Ok(views.html.index("Welcome to WebOligos", submitForm, jobForm))
  }

  // Form submission action
  @Transactional
  def submit = Action { implicit request =>
    submitForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index("Welcome to WebOligos", formWithErrors, jobForm)),
      job => {
        val jobId: Long = counter.nextCount()
        val oligoJob = new OligoJob(jobId, job._1, job._2, job._3, job._4, job._5, job._6, job._7, job._8, job._9, job._10, job._11,
          job._12.getOrElse(""), Library.Phase.INITIALIZING, null, null, null)
        db.store(oligoJob)
        queueController.add(oligoJob)
        Redirect(routes.WebOligosController.view(jobId)).flashing("created" -> "true", "success" -> "Job was successfully created and is pending processing.")
      }
    )
  }

  // job lookup form
  def getJob = Action { implicit request =>
    jobForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index("Welcome to WebOligos", submitForm, jobForm)),
      jobId => {
        Redirect(routes.WebOligosController.view(jobId.toLong)).flashing("found" -> "true", "success" -> "Job was found in the database. Retrieving record...")
      }
    )
  }

  // /view/id request
  @Transactional
  def view(jobId: Long) = Action { implicit request =>
    val job: Option[OligoJob] = db.get(jobId.toLong)
    if(job.isEmpty) {
      Redirect(routes.WebOligosController.index()).flashing("error" -> "jobNotFound")
    } else {
      Ok(views.html.result("Viewing job #".concat(jobId.toString), job.get, submitForm))
    }
  }

  // /test form for fillin
  def test = Action { implicit request =>
    Ok(views.html.index("Test Form",
      submitForm.fill((
        "ATGGCTAGCAAAGGAGAAGAACTTTTCACTGGAGTTGTCCCAATTCTTGTTGAATTAGATGGTGATGTTAATGGGCACAAATTTTCTGTCAGTGGAGAGGGTGAAGGTGATGCTACATACGGAAAGCTTACCCTTAAATTTATTTGCACTACTGGAAAACTACCTGTTCCATGGCCAACACTTGTCACTACTTTCTCTTATGGTGTTCAATGCTTTTCCCGTTATCCGGATCATATGAAACGGCATGACTTTTTCAAGAGTGCCATGCCCGAAGGTTATGTACAGGAACGCACTATATCTTTCAAAGATGACGGGAACTACAAGACGCGTGCTGAAGTCAAGTTTGAAGGTGATACCCTTGTTAATCGTATCGAGTTAAAAGGTATTGATTTTAAAGAAGATGGAAACATTCTCGGACACAAACTCGAGTACAACTATAACTCACACAATGTATACATCACGGCAGACAAACAAAAGAATGGAATCAAAGCTAACTTCAAAATTCGCCACAACATTGAAGATGGATCCGTTCAACTAGCAGACCATTATCAACAAAATACTCCAATTGGCGATGGCCCTGTCCTTTTACCAGACAACCATTACCTGTCGACACAATCTGCCCTTTCGAAAGATCCCAACGAAAAGCGTGACCACATGGTCCTTCTTGAGTTTGTAACTGCTGCTGGGATTACACATGGCATGGATGAGCTCTACAAATAA",
        180,
        30,
        4,
        36,
        0,
        -36,
        Some(new Codon("CTA", .105, .8, 4)), Some(new Codon("TCG", .16, .95, 3)), None, None,
        Option("GCTAGC,TGTACA,TTCGAA,TGGCCA,CCATGG,GCTAGC,CTCGAG,VCTCGAGB,CCWWGG,CTCGAG,CTCGAG,AGGAGG")
      )),
      jobForm
    ))
  }

}
