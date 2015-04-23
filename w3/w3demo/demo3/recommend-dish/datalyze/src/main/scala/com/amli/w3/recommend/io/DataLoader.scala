package com.amli.w3.recommend.io

import java.sql.ResultSet
import scala.io.Source._

import org.apache.spark.rdd.RDD

import com.amli.w3.recommend.Logging
import com.amli.w3.recommend.model.{EmptyTransaction, Transaction}

/**
 * Load data from a kind of Loader. Currently only `MySQLLoader` is implemented. Other loaders such
 * as `HDFSLoader` will be implemented in next release.
 */
object DataLoader extends Logging {

  /**
   * Count all food orders from loader.
   */
  def getOrderCount(sqlLoader: MySQLLoader): Int = {
    val mapRow: (ResultSet) => Int = rs => rs.getInt(1)
    val qStatement = "SELECT count(*) FROM foodorder;"
    sqlLoader.query[Int](qStatement, mapRow).first()
  }

  /**
   * Get all transactions from loader.
   */
  def getTransactions(sqlLoader: MySQLLoader): RDD[(Int, Int)] = {
    val mapRow: (ResultSet) => (Int, Int) = rs => (rs.getInt(1), rs.getInt(2))
    val qStatement = "SELECT foodid, foodorderid FROM foodorderdetail;"
    sqlLoader.query[(Int, Int)](qStatement, mapRow)
  }

  /**
   * Get the mapping from foodid to foodname.
   */
  def getDictionary(sqlLoader: MySQLLoader): RDD[(Int, String)] = {
    val mapRow: (ResultSet) => (Int, String) = rs => (rs.getInt(1), rs.getString(2))
    val qStatement = " SELECT foodid, foodname FROM food;"
    sqlLoader.query[(Int, String)](qStatement, mapRow)
  }

  /**
   * Get current restaurant Id.
   */
  def getRestaurantId(sqlLoader: MySQLLoader): Int = {
    val mapRow: (ResultSet) => Int = rs => rs.getInt(1)
    val qStatement = "SELECT companyid FROM company;"
    sqlLoader.query[Int](qStatement, mapRow).first()
  }

  /**
   * Get all tables from a given database.
   */
  def getTables(sqlLoader: MySQLLoader): RDD[String] = {
    val mapRow: (ResultSet) => String = rs => rs.getString(1)
    val qStatement = "SHOW TABLES;"
    sqlLoader.query[String](qStatement, mapRow)
  }

  /**
   * Get a function of `tableName`, which gets all column names of the table.
   */
  def getColumns(sqlLoader: MySQLLoader): (String) => RDD[String] = {
    tableName =>
      val mapRow: (ResultSet) => String = rs => rs.getString(1)
      val qStatement = s"SELECT column_name FROM information_schema.columns " +
        s"WHERE table_name = $tableName AND table_schema = ${sqlLoader.dbName};"
      sqlLoader.query[String](qStatement, mapRow)
  }

  /**
   * Get a function of `(tableName, columnName)`, which count the non-null elements in this column.
   */
  def getColumnNotNullCount(sqlLoader: MySQLLoader): (String, String) => Int = {
    (tableName, columnName) =>
      val mapRow: (ResultSet) => Int = rs => rs.getInt(1)
      val qStatement = s"SELECT COUNT($columnName) FROM $tableName WHERE $columnName IS NOT NULL;"
      sqlLoader.query[Int](qStatement, mapRow).first()
  }

  /**
   * Get a function of `(tableName, columnName)`, which count all elements in this column.
   */
  def getColumnCount(sqlLoader: MySQLLoader): (String, String) => Int = {
    (tableName, columnName) =>
      val mapRow: (ResultSet) => Int = rs => rs.getInt(1)
      val qStatement = s"SELECT COUNT($columnName) FROM $tableName;"
      sqlLoader.query[Int](qStatement, mapRow).first()
  }

  /**
   * Get `(itemId, transactionId)` or `(itemId, transactionId, userId)` from a file.
   */
  def getTransDataFromFile(transFile: String): Seq[Transaction] = {
    fromFile(transFile).getLines().map { line =>
      val words = line.split("\\s+")
      if (words.size < 2) {
        logError(s"Broken line of transaction file: $line")
        EmptyTransaction
      } else if (words.size == 2) {
        Transaction(words(1).toInt, words(1).toInt, words(0).toInt)
      } else {
        Transaction(words(1).toInt, words(2).toInt, words(0).toInt)
      }
    }.toSeq
  }

  /**
   * Get all orders of each user.
   */
  def getOrderSetOfUsers(trans: Seq[Transaction]): Map[Int, Seq[Int]] = {
    trans.map(tran => (tran.user, tran.item))
      .groupBy(_._1).map { case (u, orders) =>
      (u, orders.unzip._2)
    }
  }

  /**
   * Get all orders of each transaction.
   */
  def getOrderSetOfTransactions(trans: Seq[Transaction]): Map[Int, (Seq[Int], Seq[Int])] = {
    trans.map { case tran => (tran.transaction, (tran.user, tran.item)) }
      .groupBy(_._1).map { case (t, orders) =>
        val ui = orders.unzip._2
        val users = ui.unzip._1.toSet.toSeq
        val items = ui.unzip._2.toSet.toSeq
        (t, (users, items))
    }
  }
}
