package com.amli.w3.recommend.io

import org.apache.spark.sql._

/**
 * Generate a feature data given an RDD of `Row`, which is used for data transformation.
 */
case class FeaturedData(sqlCtx: SQLContext, dat: SchemaRDD, tableName: String) {
  dat.registerTempTable(tableName)

  /**
   * Given a SQL transformation (with UDF), transform a `FeaturedData` to another one.
   */
  def transform(transformer: String, otherTableName: String): FeaturedData = {
    new FeaturedData(sqlCtx, sqlCtx.sql(transformer), otherTableName)
  }
}

