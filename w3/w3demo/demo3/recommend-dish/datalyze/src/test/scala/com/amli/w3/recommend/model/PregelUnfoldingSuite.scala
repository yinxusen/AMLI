package com.amli.w3.recommend.model

import java.io.PrintWriter
import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}

import org.apache.spark.graphx.Graph

import com.amli.w3.recommend.scLocal._

class PregelUnfoldingSuite extends FunSuite with Matchers with BeforeAndAfter {

  val transFile = "/tmp/transaction.txt"

  before {
    val out = new PrintWriter(transFile)
    for (i <- 1 to 100) {
      out.println(s"${i % 20} ${i % 80}")
    }
    out.close()
  }

  test("test pregel unfolding") {
    val rawEdges = sc.textFile(transFile, 2).map(s =>
      s.split("\\s+").head.toLong -> s.split("\\s+").last.toLong)
    val graph = Graph.fromEdgeTuples(rawEdges, -1)
    val puGraph = PregelUnfolding.run(graph, 5)

    // TODO: What should I do for test its result?
    for ((a, NodeAttr(b, c, d, e, f)) <- puGraph.vertices.collect()) {
      println(s"my id is $a")
      println(s"my neighbors are ${b.mkString(",")}")
      println(s"my community is ${c.mkString(",")}")
      println(s"my outer links count is $d")
      println(s"my inner links count is $e")
      println(s"largest mod gain is $f")
      println()
    }
  }

  after { /** do nothing*/ }
}
