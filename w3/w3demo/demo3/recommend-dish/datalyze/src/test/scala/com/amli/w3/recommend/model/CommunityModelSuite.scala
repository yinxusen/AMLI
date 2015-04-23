package com.amli.w3.recommend.model

import java.io.PrintWriter

import org.scalatest.{BeforeAndAfter, FunSuite}

import com.amli.w3.recommend.io.DataLoader._
import com.amli.w3.recommend.scLocal._

class CommunityModelSuite extends FunSuite with BeforeAndAfter {
  val lambda = 0.8
  val numIterations = 10
  val transFile = "/tmp/transaction.txt"
  val modelFile = "/tmp/model.txt"

  before {
    val out = new PrintWriter(transFile)
    for (i <- 1 to 100) {
      out.println(s"${i % 20} ${i % 80}")
    }
    out.close()
  }

  test("test train model") {
    val model = new CommunityModel(sc)
    val trans = getTransDataFromFile(transFile)
    model.configModel(Map(("lambda", lambda.toString), ("numIterations", numIterations.toString)))
    assert(model.lambda === lambda)
    assert(model.numIterations === numIterations)

    model.trainModel(trans)

    model.exportModel(modelFile)
    val readModel = new CommunityModel(sc)
    readModel.importModel(modelFile)

    assert(model.lambda === readModel.lambda)
    // TODO: how to test model training?
  }

  after {
    /** Not need to delete temporal files manually */
  }
}
