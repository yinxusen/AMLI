package org.template.recommendation

import io.prediction.controller.PDataSource
import io.prediction.controller.EmptyEvaluationInfo
import io.prediction.controller.EmptyActualResult
import io.prediction.controller.Params
import io.prediction.data.storage.Event
import io.prediction.data.storage.Storage

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD

import grizzled.slf4j.Logger

case class DataSourceParams(trainSet: String) extends Params
case class TrainData(mid: String, uid: String, score: Double, date: String)

class DataSource(val dsp: DataSourceParams)
  extends PDataSource[TrainingData,
      EmptyEvaluationInfo, Query, EmptyActualResult] {

  @transient lazy val logger = Logger[this.type]

  override
  def readTraining(sc: SparkContext): TrainingData = {
    val trainSet = sc.textFile(dsp.trainSet).map(_.split(",")).map {
      m => TrainData(m(0), m(1), m(2).toDouble, m(3))
    }
    val ratingsRDD = trainSet.map(td => Rating(td.uid, td.mid, td.score))

    new TrainingData(ratingsRDD)
  }
}

case class Rating(
  user: String,
  item: String,
  rating: Double
)

class TrainingData(
  val ratings: RDD[Rating]
) extends Serializable {
  override def toString = {
    s"ratings: [${ratings.count()}] (${ratings.take(2).toList}...)"
  }
}
