package model

import java.util
import javax.persistence.Id

import com.avaje.ebean.Model
import play.data.validation.Constraints
import edu.tcnj.oligos.library.Library.Phase
import edu.tcnj.oligos.library._
import edu.tcnj.oligos.ui.Runner
import services.JavaHelper

class OligoJob(
                @Id var id: Long,
                @Constraints.Required var rna: String,
                @Constraints.Required var oligoLength: Int,
                @Constraints.Required var overlapSize: Int,
                @Constraints.Required var overlapDiffs: Int,
                @Constraints.Required var start: Int,
                @Constraints.Required var end: Int,
                @Constraints.Required var offset: Int,
                @Constraints.Required var codon0: Option[Codon],
                @Constraints.Required var codon1: Option[Codon],
                @Constraints.Required var codon2: Option[Codon],
                @Constraints.Required var codon3: Option[Codon],
                @Constraints.Required var restrictions: String,
                @Constraints.Required var phase: Phase,
                var results: ResultLibrary,
                @transient var runner: Runner
              )
  extends Model {

}

case class ResultLibrary(var lib: Library) {
  var oligos: util.Map[Integer, util.List[Oligo]] = lib.getOligos
  @transient var geneSeq: util.List[Sequence] = LibraryUtils.buildPermutations(
      new Fragment.Range(0, lib.getSize - 1), lib.getOligos, lib.getOligoLength, lib.getOverlapLength
  )
  var genes: util.List[Gene] = JavaHelper.seqToGene(geneSeq, lib)
}