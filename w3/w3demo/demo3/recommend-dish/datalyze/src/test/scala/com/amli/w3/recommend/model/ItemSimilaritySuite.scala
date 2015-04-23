package com.amli.w3.recommend.model

import java.io.PrintWriter
import org.scalatest.{BeforeAndAfter, FunSuite, Matchers}
import com.amli.w3.recommend.io.DataLoader._
import com.amli.w3.recommend.model.Utils._

class ItemSimilaritySuite extends FunSuite with Matchers with BeforeAndAfter {

  val config = Config(transFile = "/tmp/transaction.txt", modelFile = "/tmp/model.txt")

  before {
    val out = new PrintWriter(config.transFile)
    for (i <- 1 to 100) {
      out.println(s"${i % 20} ${i % 80}")
    }
    out.close()
  }

  test("test train model") {
    val model = new ItemSimilarity()
    val trans = getTransDataFromFile(config.transFile)
    model.configModel(Map(("K", config.k.toString)))
    assert(model.k === config.k)

    model.trainModel(trans)

    model.exportModel(config.modelFile)
    val readModel = new ItemSimilarity()
    readModel.importModel(config.modelFile)

    assert(model.k === readModel.k)
    model.similarities should equal(readModel.similarities)
    model.popularityModel.popularities should equal (readModel.popularityModel.popularities)
    model.popularityModel.orderHistory should equal (readModel.popularityModel.orderHistory)
  }

  after {
    /** Not need to delete temporal files manually */
  }
}
