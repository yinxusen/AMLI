package com.amli.w3

import org.apache.spark.sql.SQLContext
import org.apache.spark.{SparkContext, SparkConf}

package object recommend {

  object scLocal {
    val scConf = new SparkConf().setMaster("local[4]").setAppName("Assist Spark Context")
    val sc = new SparkContext(scConf)
    val sqlCtx = new SQLContext(sc)
  }
}
