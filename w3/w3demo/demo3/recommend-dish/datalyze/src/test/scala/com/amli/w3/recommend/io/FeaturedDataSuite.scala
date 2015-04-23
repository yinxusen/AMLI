package com.amli.w3.recommend.io

import org.scalatest.{Matchers, FunSuite}

import com.amli.w3.recommend._

case class DataPoint(xPoint: Int, yPoint: Int)

class FeaturedDataSuite extends FunSuite with Matchers {
  test("test the functionality of SchemaRDD in FeaturedData") {
    val dataLocal = Seq(
      DataPoint(1, 2),
      DataPoint(3, 4),
      DataPoint(5, 6),
      DataPoint(7, 8)
    )

    import scLocal._
    import sqlCtx._

    val data = sc.parallelize[DataPoint](dataLocal)
    val feature = FeaturedData(sqlCtx, data.toSchemaRDD, "Graph")

    val avgPoint: (Int, Int) => Double = (l, r) => (l + r) / 2.0
    sqlCtx.registerFunction("avgPoint", avgPoint)
    val avg = feature.transform(s"select avgPoint(xPoint, yPoint) from Graph", "AVG")
    avg.dat.map(_.getDouble(0)).collect() should contain allOf (1.5, 3.5, 5.5, 7.5)
  }
}
