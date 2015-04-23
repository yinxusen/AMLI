package com.amli.w3.recommend.model

import scala.collection.mutable.{Map => MutableMap}
import scala.io.Source.fromFile
import java.io.PrintWriter
import collection.immutable.ListMap

import com.amli.w3.recommend.io.DataTransfer._
import com.amli.w3.recommend.io.CliqueStat

/**
 * Item similarity model.
 */
class ItemSimilarity extends Model with ModelLike {

  /**
   * Number of items referenced when computing similarity.
   */
  var k: Int = 20

  /**
   * Similarity matrix with weights.
   */
  var similarities = Map[Int, Map[Int, Double]]()

  /**
   * A mix-in popularity model for recommendation call-back in case of cold start.
   */
  val popularityModel = new PopularityModel()

  override def trainModel(trans: Seq[Transaction]): ModelLike = {
    popularityModel.trainModel(trans)
    import popularityModel._

    val clique = new CliqueStat(removeUserFromTransaction(trans))
    val coMatrix = clique.getNClique(2).flatMap { case (Seq(u, v), c) =>
      Map((u, v) -> c, (v, u) -> c)
    }

    similarities = coMatrix.map { case ((u, v), c) =>
      ((u, v), c / math.sqrt(popularities(u) * popularities(v)))
    }.groupBy(_._1._1).map { case (k, l) =>
      (k, ListMap(l.map { case ((u, v), c) =>
        (v, c)
      }.toSeq.sortBy(_._2).reverse: _*))
    }.map { case (k, l) =>
      (k, l.map { case (v, c) => (v, c / l.head._2) })
    }

    this
  }

  override def exportModel(outFile: String): ModelLike = {
    val out = new PrintWriter(outFile)
    out.println(s"comment: $comment")
    out.println(s"k: $k")
    for ((itemA, relatedItems) <- similarities) {
      for ((itemB, sim) <- similarities(itemA)) {
        out.println(s"similarity: $itemA $itemB $sim")
      }
    }
    out.close()
    popularityModel.exportModel(outFile)
    this
  }

  override def importModel(inFile: String): ModelLike = {
    val tmpSimilarities = MutableMap[Int, Map[Int, Double]]()

    val lineIterator = fromFile(inFile).getLines()
    for (line <- lineIterator) {
      val words = line.split("\\s+")
      if (words(0) == "comment:") comment = words(1)
      if (words(0) == "k:") k = words(1).toInt
      if (words(0) == "similarity:") {
        val (itemA, itemB, sim) =
          (words(1).toInt, words(2).toInt, words(3).toDouble)
        if (tmpSimilarities.contains(itemA)) {
          tmpSimilarities(itemA) += (itemB -> sim)
        } else {
          tmpSimilarities += (itemA -> Map(itemB -> sim))
        }
      }
    }
    val sortedSimilarities = tmpSimilarities.map { case (user, items) =>
      (user, ListMap(items.toSeq.sortBy(_._2).reverse:_*))
    }
    similarities = sortedSimilarities.toMap
    popularityModel.importModel(inFile)
    this
  }

  override def configModel(params: Map[String, String]): ModelLike = {
    params.map {
      case ("k", v) =>
        val tmp = scala.util.Try(v.toInt).toOption
        if (tmp.isDefined) k = tmp.get
      case ("comment", c) =>
        comment = c
      case _ =>
    }
    this
  }

  override def recommend(users: Seq[Int], itemsInCart: Seq[Int], num: Int): Seq[RecommendResult] = {
    import popularityModel._

    var rank = MutableMap[Int, (Double, Int, Double)]()
    val allRefItems = (orderHistoryOfUsers(users) ++ itemsInCart).toSet

    for (i <- allRefItems) {
      if (similarities.contains(i)) {
        for ((j, sim) <- similarities(i).slice(0, k)) {
          if (!itemsInCart.contains(j)) {
            if (rank.contains(j)) {
              if (sim > rank(j)._3) rank(j) = (rank(j)._1 + sim, i, sim)
              else rank(j) = (rank(j)._1 + sim, rank(j)._2, rank(j)._3)
            } else {
              rank += (j -> (sim, i, sim))
            }
          }
        }
      }
    }
    val results = ListMap(rank.toSeq.sortBy(_._2._1).reverse:_*)
      .slice(0, num).map { case (j, (weight, i, wi)) =>
        if (itemsInCart.contains(i)) {
          new RecommendResult(j, weight, s"You ordered $i")
        } else {
          new RecommendResult(j, weight, s"You ever ordered $i")
        }
    }.toSeq
    results ++ popularityModel.recommend(users, itemsInCart, num - results.size)
  }
}
