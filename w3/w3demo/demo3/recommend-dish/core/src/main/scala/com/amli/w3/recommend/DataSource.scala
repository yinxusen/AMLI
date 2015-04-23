package com.amli.w3.recommend

import _root_.io.prediction.controller._
import org.apache.spark.SparkContext

import com.amli.w3.recommend.io.{DataLoader, MySQLLoader}

case class DataSourceParamUnit(sources: Seq[String], users: Seq[String], passwords: Seq[String])
case class DataSourceParams(params: Seq[DataSourceParamUnit]) extends Params

class DataSource(val dataSourceParams: DataSourceParams)
  extends PDataSource[DataSourceParams, EmptyDataParams, TrainingData, Query, EmptyActualResult] {

  override def readTraining(sc: SparkContext): TrainingData = {
    TrainingData {
      dataSourceParams.params.map { case DataSourceParamUnit(sources, users, passwords) =>
        val (mergedRestaurants, mergedData) = sources.zip(users.zip(passwords))
          .foldLeft((Seq.empty[Int], TrainingUnit.empty)) {
          case ((restaurantIds, trainingData), (source, (user, password))) =>
            val sqlLoader = MySQLLoader(sc, source, user, password)
            val trans = DataLoader.getTransactions(sqlLoader).collect()
            val restaurantId = DataLoader.getRestaurantId(sqlLoader)
            (restaurantIds :+ restaurantId, trainingData + TrainingUnit(trans))
        }
        (mergedRestaurants, mergedData)
      }
    }
  }
}

case class TrainingUnit(trans: Seq[(Int, Int)]) extends Serializable {
  def +(other: TrainingUnit) = new TrainingUnit(this.trans ++ other.trans)
}

object TrainingUnit {
  def empty = new TrainingUnit(Nil)
}

case class TrainingData(data: Seq[(Seq[Int], TrainingUnit)]) extends Serializable {
  override def toString = s"Training data contains ${data.size} training units. " +
    s"The first training unit contains ${data.head._2.trans.size} training data."
}
