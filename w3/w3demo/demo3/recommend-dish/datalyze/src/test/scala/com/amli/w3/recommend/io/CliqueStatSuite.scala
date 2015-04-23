package com.amli.w3.recommend.io

import org.scalatest.{Matchers, FunSuite}

class CliqueStatSuite extends FunSuite with Matchers {
  test("test get n cliques") {
    val transactions = Seq(
      1 -> 1,
      1 -> 2,
      1 -> 3,
      1 -> 4,
      2 -> 2,
      2 -> 3,
      2 -> 4,
      3 -> 3,
      3 -> 4,
      4 -> 4
    )
    val cliques = new CliqueStat(transactions)
    val twoCliques = cliques.getNClique(2)
    val realTwoCliquesResult = Map(
      (List(3, 4),1),
      (List(1, 2),3),
      (List(2, 3),2),
      (List(1, 4),1),
      (List(2, 4),1),
      (List(1, 3),2)
    )
    twoCliques should equal (realTwoCliquesResult)

    val threeCliques = cliques.getNClique(3)
    val realThreeCliquesResult = Map(
      (List(1, 2, 4),1),
      (List(2, 3, 4),1),
      (List(1, 2, 3),2),
      (List(1, 3, 4),1)
    )
    threeCliques should equal (realThreeCliquesResult)
  }
}
