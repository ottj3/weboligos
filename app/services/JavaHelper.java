package services;

import edu.tcnj.oligos.library.Gene;
import edu.tcnj.oligos.library.Library;
import edu.tcnj.oligos.library.Sequence;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public final class JavaHelper {

    public static List<Gene> seqToGene(List<Sequence> seq, Library lib) {
        return seq.stream().map(
                    s -> Gene.fromSequence(s, lib.getCodonsOfInterest()))
                .collect(Collectors.toCollection(ArrayList::new));
    }
}
