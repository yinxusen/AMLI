package com.amli.w3.recommend

import _root_.io.prediction.controller._
import org.apache.spark.SparkContext

class Preparator extends PPreparator[EmptyPreparatorParams, TrainingData, PreparedData] {

  override def prepare(sc: SparkContext, trainingData: TrainingData): PreparedData = {
    PreparedData(trainingData.data)
  }
}

case class PreparedData(data: Seq[(Seq[Int], TrainingUnit)]) extends Serializable {
  override def toString = s"Prepared data contains ${data.size} training units."
}
