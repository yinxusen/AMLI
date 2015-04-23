package com.amli.w3.recommend

import _root_.io.prediction.controller._

import com.amli.w3.recommend.io.DataTransfer._
import com.amli.w3.recommend.model.ItemSimilarity

case class ItemToItemParamUnit(k: Int, modelFile: String)
case class ItemToItemParams(params: Seq[ItemToItemParamUnit]) extends Params

class ItemToItem(val itemToItemParams: ItemToItemParams)
    extends PAlgorithm[ItemToItemParams, PreparedData, ItemToItemModel, Query, PredictedResult] {

  override def train(data: PreparedData): ItemToItemModel = {
    val models = itemToItemParams.params.zip(data.data).flatMap {
      case (ItemToItemParamUnit(k, modelFile), (restaurantIds, TrainingUnit(trans))) =>
        val model = new ItemSimilarity()
        model.configModel(Map("k" -> k.toString, "comment" -> restaurantIds.mkString(",")))
        model.trainModel(addVirtualUserToTransaction(trans))
        var i = 0
        var res: Seq[(Int, ItemSimilarity, Option[String])] = Nil
        while (i < restaurantIds.size) {
          if (i == 0) res = res.+:((restaurantIds(i), model, Option(modelFile)))
          else res = res.+:((restaurantIds(i), model, None))
          i += 1
        }
        res
    }
    ItemToItemModel(
      models.map { case (id, model, store) => (id, model) }.toMap,
      models.map { case (id, model, store) => (id, store) }.toMap
    )
  }

  override def predict(model: ItemToItemModel, query: Query): PredictedResult = {
    val results = {
      if (!model.models.contains(query.restaurantId)) Nil
      else model.models(query.restaurantId)
        .recommend(query.users.toSeq, query.itemsInCart.toSeq, query.num)
    }
    new PredictedResult(results.map (r => RecommendItem(r.item, r.score, r.reason)).toArray)
  }
}
