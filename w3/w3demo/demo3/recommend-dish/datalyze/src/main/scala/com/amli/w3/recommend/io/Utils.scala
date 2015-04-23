package com.amli.w3.recommend.io

object Utils {
  val dbConnStr: (String) => String = dbName => s"jdbc:mysql://localhost:3306/$dbName"
  val dbNames = Array("jts", "hbo", "qyf", "qjlx", "gwx")
  val dbMap = dbNames.map( dbName => dbName -> (dbConnStr(dbName), "root", "root")).toMap
}
