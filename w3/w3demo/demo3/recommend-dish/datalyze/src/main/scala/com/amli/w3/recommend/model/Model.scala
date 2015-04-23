package com.amli.w3.recommend.model

case class RecommendResult(item: Int, score: Double, reason: String)
case class Transaction(transaction: Int, user: Int, item: Int)
object EmptyTransaction extends Transaction(-1, -1, -1)

/**
 * Define the behaviors of a `Model`. All classes that implement these interfaces is a `Model`.
 */
trait ModelLike {
  def trainModel(trans: Seq[Transaction]): ModelLike
  def exportModel(outFile: String): ModelLike
  def importModel(inFile: String): ModelLike
  def configModel(params: Map[String, String]): ModelLike
  def recommend(users: Seq[Int], itemsInCart: Seq[Int], num: Int): Seq[RecommendResult]
}

/**
 * Define all common attributes of a `Model`.
 */
abstract class Model {
  var comment: String = "fake comment"
}
