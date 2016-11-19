package controllers

import javax.inject._

import play.api.data._
import play.api.data.Forms._
import play.api.data.format.Formats._
import play.api.mvc._
import play.api.data.validation.Constraints._
import play.api.i18n.{ MessagesApi, I18nSupport }

/**
  * Represents a Codon of interest
  *
  * @param name IUPAC 3-letter nucleotide
  * @param min decimal of minimum representation
  * @param max decimal of maximum representation
  * @param levels number of intervals between min and max to express
  */
case class Codon(name: String, min: Double, max: Double, levels: Int)

/**
 * This controller creates an `Action` to handle HTTP requests to the
 * application's home page.
 * It also takes POST requests from form submissions to handle user
 * input.
 */
@Singleton
class WebOligosController @Inject() (val messagesApi: MessagesApi) extends Controller with I18nSupport {

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

  def submit = Action { implicit request =>
    submitForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index("Error with form submission.", formWithErrors, jobForm)),
      job => {
        val jobId = "123456"
        System.out.println(job.toString())
        Redirect(routes.WebOligosController.view(jobId))
      }
    )
  }

  def getJob = Action { implicit request =>
    jobForm.bindFromRequest.fold(
      formWithErrors => BadRequest(views.html.index("Error with form submission.", submitForm, jobForm)),
      jobId => {
        Redirect(routes.WebOligosController.view(jobId))
      }
    )
  }

  def view(jobId: String) = Action {
    //Ok(views.html.view(jobId))
    Ok(views.html.index("Viewing job ".concat(jobId), submitForm, jobForm))
  }

  def test = Action {
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
