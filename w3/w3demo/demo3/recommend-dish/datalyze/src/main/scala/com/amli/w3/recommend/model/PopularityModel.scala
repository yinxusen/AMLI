package com.amli.w3.recommend.model

import java.io.{File, FileWriter, PrintWriter}
import com.amli.w3.recommend.io.CliqueStat

import scala.io.Source.fromFile
import scala.collection.mutable.{Map => MutableMap}
import scala.collection.immutable.ListMap

import com.amli.w3.recommend.io.DataTransfer._
import com.amli.w3.recommend.io.DataLoader._

class PopularityModel extends Model with ModelLike {
  /**
   * Popularity of all items.
   */
  var popularities = Map[Int, Int]()

  /**
   * Order history of each user or transaction.
   */
  var orderHistory = Map[Int, Seq[Int]]()

  override def trainModel(trans: Seq[Transaction]): ModelLike = {
    val clique = new CliqueStat(removeUserFromTransaction(trans))
    popularities = ListMap(clique.getNClique(1).map {
      case (Seq(u), c) => (u, c)
    }.toSeq.sortBy(_._2).reverse:_*).toMap
    orderHistory = getOrderSetOfUsers(trans)
    this
  }

  override def exportModel(outFile: String): ModelLike = {
    val out = new PrintWriter(new FileWriter(new File(outFile), true))
    for ((user, ordered) <- orderHistory) {
      for (item <- ordered) out.println(s"order: $user $item")
    }
    for ((item, pop) <- popularities) {
      out.println(s"popularity: $item $pop")
    }
    out.close()
    this
  }

  override def importModel(inFile: String): ModelLike = {
    val tmpOrderHistory = MutableMap[Int, Seq[Int]]()
    val tmpPopularities = MutableMap[Int, Int]()

    val lineIterator = fromFile(inFile).getLines()
    for (line <- lineIterator) {
      val words = line.split("\\s+")
      if (words(0) == "order:") {
        val (user, item) = (words(1).toInt, words(2).toInt)
        if (tmpOrderHistory.contains(user)) tmpOrderHistory(user) ++= Seq(item)
        else tmpOrderHistory += (user -> Seq(item))
      }
      if (words(0) == "popularity:") {
        val (item, pop) = (words(1).toInt, words(2).toInt)
        tmpPopularities += (item -> pop)
      }
    }
    val sorted = ListMap(tmpPopularities.toSeq.sortBy(_._2).reverse:_*)
    orderHistory = tmpOrderHistory.toMap
    popularities = sorted.toMap
    this
  }

  override def configModel(params: Map[String, String]): ModelLike = { this }

  override def recommend(users: Seq[Int],
      itemsInCart: Seq[Int],
      num: Int): Seq[RecommendResult] = {
    popularities.slice(0, num).map { case (item, pop) =>
      new RecommendResult(item, pop.toDouble, "Most popular")
    }.toSeq
  }

  def orderHistoryOfUsers(users: Seq[Int]): Seq[Int] = {
    users.flatMap { case user => orderHistory.getOrElse(user, Nil) }
  }
}
