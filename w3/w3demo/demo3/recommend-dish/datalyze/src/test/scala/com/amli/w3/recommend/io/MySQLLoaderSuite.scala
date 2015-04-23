package com.amli.w3.recommend.io

import java.sql.{SQLException, DriverManager, ResultSet}
import org.scalatest.{BeforeAndAfter, Matchers, FunSuite}

import com.amli.w3.recommend._

class MySQLLoaderSuite extends FunSuite with Matchers with BeforeAndAfter {
  before {
    Class.forName("org.apache.derby.jdbc.EmbeddedDriver")
    val conn = DriverManager.getConnection("jdbc:derby:target/MySQLLoaderSuiteDB;create=true")
    try {
      val create = conn.createStatement
      create.execute("""
        CREATE TABLE FOO(
          ID INTEGER NOT NULL GENERATED ALWAYS AS IDENTITY (START WITH 1, INCREMENT BY 1),
          DATA INTEGER
        )""")
      create.close()
      val insert = conn.prepareStatement("INSERT INTO FOO(DATA) VALUES(?)")
      (1 to 100).foreach { i =>
        insert.setInt(1, i * 2)
        insert.executeUpdate
      }
      insert.close()
    } catch {
      case e: SQLException if e.getSQLState == "X0Y32" =>
      // table exists
    } finally {
      conn.close()
    }
  }

  test("test mysql data loader") {
    import scLocal._
    val mapRow: (ResultSet) => (Int, Int) = rs => (rs.getInt(1), rs.getInt(2))
    val conn = "jdbc:derby:target/MySQLLoaderSuiteDB;create=true"
    val qStatement = "SELECT ID, DATA FROM FOO WHERE ID=10"
    val data = MySQLLoader(sc, conn).query[(Int, Int)](qStatement, mapRow)
    data.collect() should contain only ((10, 20))
  }

  after {
    try {
      DriverManager.getConnection("jdbc:derby:;shutdown=true")
    } catch {
      case se: SQLException if se.getSQLState == "XJ015" =>
      // normal shutdown
    }
  }
}
