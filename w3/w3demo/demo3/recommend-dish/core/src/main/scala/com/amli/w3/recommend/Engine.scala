package com.amli.w3.recommend

import _root_.io.prediction.controller._

case class Query(
  restaurantId: Int,
  users: Array[Int],
  personAmount: Int,
  expectedConsumePerHead: Int,
  mealType: Int,
  itemsInCart: Array[Int],
  clickedItems: Array[Int],
  num: Int
) extends Serializable

case class PredictedResult(
  recommendItems: Array[RecommendItem]
) extends Serializable

case class RecommendItem(
  id: Int,
  score: Double,
  reason: String
) extends Serializable

object RecommendationEngine extends IEngineFactory {
  override def apply() = {
    new Engine(
      classOf[DataSource],
      classOf[Preparator],
      Map("itemtoitem" -> classOf[ItemToItem]),
      classOf[Serving])
  }
}
