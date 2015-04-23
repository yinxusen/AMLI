package com.amli.w3.recommend.io

import com.amli.w3.recommend.model.Transaction

/**
 * Transfer data from one format to another.
 */
object DataTransfer {
  // TODO: Combine `DataTransfer` with `SchemaRDD` and `ML` package for unified transformation.

  /**
   * Map a transaction to a virtual user.
   */
  def transactionToVirtualUser(transaction: Int): Int = {
    // TODO: make sure that the virtualUser will not be conflict to real user
    transaction
  }

  /**
   * Add virtual users to `(itemId, transactionId)` pairs.
   */
  def addVirtualUserToTransaction(trans: Seq[(Int, Int)]): Seq[Transaction] = {
    trans.map { case (item, transaction) =>
      Transaction(transaction, transactionToVirtualUser(transaction), item)
    }
  }

  /**
   * Remove `userId`s from a sequence of `Transaction`.
   */
  def removeUserFromTransaction(trans: Seq[Transaction]): Seq[(Int, Int)] = {
    trans.map(tran => (tran.item, tran.transaction)).distinct
  }
}
