package com.amli.w3.recommend.io

import java.sql.{DriverManager, ResultSet}
import scala.collection.mutable
import scala.reflect.ClassTag

import org.apache.spark.SparkContext
import org.apache.spark.rdd.RDD

/**
 * Interface that reading data from MySQL.
 */
import com.amli.w3.recommend.Logging

class MySQLLoader(sc: SparkContext, connStr: String, user: String, password: String)
  extends Logging {

  val dbName = connStr.split("/").last

  /**
   * Query from a mysql database with a row mapping function.
   */
  def query[T: ClassTag](
      sql: String,
      mapRow: (ResultSet) => T = MySQLLoader.resultSetToObjectArray _): RDD[T] = {
    val conn = DriverManager.getConnection(connStr, user, password)
    val stmt = conn.prepareStatement(sql, ResultSet.TYPE_FORWARD_ONLY, ResultSet.CONCUR_READ_ONLY)
    if (conn.getMetaData.getURL.matches("jdbc:mysql:.*")) {
      stmt.setFetchSize(Integer.MIN_VALUE)
    }
    val rs = stmt.executeQuery()

    val arrayBuilder = mutable.ListBuffer[T]()
    while(rs.next()) {
      arrayBuilder += mapRow(rs)
    }
    try {
      if (null != rs && ! rs.isClosed) {
        rs.close()
      }
    } catch {
      case e: Exception => logWarning("Exception closing resultset", e)
    }
    try {
      if (null != stmt && ! stmt.isClosed) {
        stmt.close()
      }
    } catch {
      case e: Exception => logWarning("Exception closing statement", e)
    }
    try {
      if (null != conn && ! conn.isClosed) {
        conn.close()
      }
      logInfo("closed connection")
    } catch {
      case e: Exception => logWarning("Exception closing connection", e)
    }
    sc.parallelize[T](arrayBuilder)
  }
}

object MySQLLoader {
  def apply(sc: SparkContext, conn: String, user: String = null, password: String = null) = {
    new MySQLLoader(sc, conn, user, password)
  }

  def apply(sc: SparkContext, dbMetaData: (String, String, String)) = {
    new MySQLLoader(sc, dbMetaData._1, dbMetaData._2, dbMetaData._3)
  }

  /**
   * Default row mapping function, which maps a row into an array of `Object`.
   */
  def resultSetToObjectArray(rs: ResultSet): Array[Object] = {
    Array.tabulate[Object](rs.getMetaData.getColumnCount)(i => rs.getObject(i + 1))
  }
}
