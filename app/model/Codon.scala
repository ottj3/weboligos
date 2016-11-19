package model

/**
  * Represents a Codon of interest
  *
  * @param name IUPAC 3-letter nucleotide
  * @param min decimal of minimum representation
  * @param max decimal of maximum representation
  * @param levels number of intervals between min and max to express
  */
case class Codon(name: String, min: Double, max: Double, levels: Int)
