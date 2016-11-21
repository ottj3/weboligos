package services;

import com.google.common.base.Joiner;
import com.google.common.collect.EnumBiMap;
import com.google.common.collect.Lists;
import edu.tcnj.oligos.data.AminoAcid;
import edu.tcnj.oligos.data.Base;
import edu.tcnj.oligos.library.*;
import model.Codon;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public final class JavaHelper {

    public static List<Gene> seqToGene(List<Sequence> seq, Library lib) {
        return seq.stream().map(
                    s -> Gene.fromSequence(s, lib.getCodonsOfInterest()))
                .collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<Codon> nonNullCodons(Codon c1, Codon c2, Codon c3, Codon c4) {
        return Lists.newArrayList(c1, c2, c3, c4).stream().filter(t -> t != null).collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<String> codonNames(List<Codon> codons) {
        return codons.stream().map(Codon::name).collect(Collectors.toCollection(ArrayList::new));
    }
    public static List<Double> codonMins(List<Codon> codons) {
        return codons.stream().map(Codon::min).collect(Collectors.toCollection(ArrayList::new));
    }
    public static List<Double> codonMaxs(List<Codon> codons) {
        return codons.stream().map(Codon::max).collect(Collectors.toCollection(ArrayList::new));
    }
    public static List<Integer> codonLevels(List<Codon> codons) {
        return codons.stream().map(Codon::levels).collect(Collectors.toCollection(ArrayList::new));
    }

    public static List<BaseSequence> restrictions(String text) {
        String[] sites = text.split(",");
        List<BaseSequence> restrictions = Lists.newArrayList();
        for (String site : sites) {
            site = site.trim().toUpperCase();
            if (site.isEmpty()) continue;
            List<Base> restriction = Lists.newArrayList();
            for (char c : site.toCharArray()) {
                try {
                    Base b = Base.valueOf(String.valueOf(c));
                    restriction.add(b);
                } catch (IllegalArgumentException e) {
                    throw new IllegalArgumentException(
                            "Unrecognized or unsupported base " + c + " in restriction sites.", e);
                }
            }
            restrictions.add(new BaseSequence(restriction));
        }
        return restrictions;
    }

    public static String oligosToOutput(Map<Integer, List<Oligo>> oligos) {
        StringBuilder sb = new StringBuilder();
        for (Map.Entry<Integer, List<Oligo>> entry : oligos.entrySet()) {
            char var = 'a';
            for (Oligo oligo : entry.getValue()) {
                List<String> deltaList = Lists.newArrayList();
                for (Map.Entry<edu.tcnj.oligos.data.Codon, Integer> deltas : oligo.getDeltas().entrySet()) {
                    deltaList.add(deltas.getKey() + " (" + deltas.getKey().getAminoAcid() + "): " + deltas.getValue());
                }
                sb.append("&gt; ").append(entry.getKey()).append(" (").append(var++).append(") [")
                        .append(Joiner.on(", ").join(deltaList)).append("]").append("\n")
                        .append(oligo).append("\n");
            }
        }
        return sb.toString();
    }

    public static String genesToOutput(List<Gene> genes, EnumBiMap<AminoAcid,edu.tcnj.oligos.data.Codon> coi) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < genes.size(); i++) {
            Gene gene = genes.get(i);
            String name = (i + 1) + " [";
            List<String> codonFreqs = Lists.newArrayList();
            for (Map.Entry<AminoAcid, Map<edu.tcnj.oligos.data.Codon, Double>> acidEntry : gene.getFreqs().entrySet()) {
                for (Map.Entry<edu.tcnj.oligos.data.Codon, Double> codonEntry : acidEntry.getValue().entrySet()) {
                    if (coi.get(acidEntry.getKey()) != codonEntry.getKey()) {
                        continue;
                    }
                    codonFreqs.add(codonEntry.getKey() + " (" + acidEntry.getKey() + ") "
                            + ((int) (codonEntry.getValue() * 100 + 0.5) + "%"));
                }
            }
            name += Joiner.on(", ").join(codonFreqs) + "]";
            sb.append("&gt; ").append(name).append(" ").append(gene.getCPS())
                .append("\n").append(gene).append("\n");
        }
        return sb.toString();
    }
}
