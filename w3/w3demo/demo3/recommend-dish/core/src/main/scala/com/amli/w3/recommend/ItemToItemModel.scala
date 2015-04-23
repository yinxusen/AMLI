package com.amli.w3.recommend

import _root_.io.prediction.controller._
import org.apache.spark.SparkContext

import com.amli.w3.recommend.model.ItemSimilarity

class ItemToItemModel(val models: Map[Int, ItemSimilarity], stores: Map[Int, Option[String]])
  extends IPersistentModel[ItemToItemParams] {

  override def save(id: String, params: ItemToItemParams, sc: SparkContext): Boolean = {
    for (restaurantId <- models.keySet) {
      val model = models(restaurantId)
      val store = stores(restaurantId)
      store match {
        case Some(something) => model.exportModel(something)
        case None => // do nothing
      }
    }
    true
  }
}

object ItemToItemModel extends IPersistentModelLoader[ItemToItemParams, ItemToItemModel] {

  def apply(models: Map[Int, ItemSimilarity], stores: Map[Int, Option[String]] = null) = {
    new ItemToItemModel(models, stores)
  }

  def parseComment(comment: String): Seq[Int] = {
    comment.split(",").map(_.toInt)
  }

  override def apply(id: String, params: ItemToItemParams, sc: Option[SparkContext]) = {
    ItemToItemModel(
      params.params.flatMap { case ItemToItemParamUnit(_, modelFile) =>
        val model = new ItemSimilarity()
        model.importModel(modelFile)
        val restaurantIds = parseComment(model.comment)
        restaurantIds.map(_ -> model)
      }.toMap
    )
  }
}
