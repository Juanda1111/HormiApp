package com.hormi.hormiapp.data.repository

import com.hormi.hormiapp.data.local.dao.TransactionDao
import com.hormi.hormiapp.data.local.entity.TransactionEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao
) {
    fun getAllTransactions(): Flow<List<TransactionEntity>> {
        return transactionDao.getAllTransactions()
    }

    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> {
        return transactionDao.getTransactionsByType(type)
    }

    fun getTotalAntExpenses(): Flow<Double?> {
        return transactionDao.getTotalAntExpenses()
    }

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        return withContext(Dispatchers.IO) {
            transactionDao.insertTransaction(transaction)
        }
    }

    suspend fun updateTransaction(transaction: TransactionEntity): Int {
        return withContext(Dispatchers.IO) {
            transactionDao.updateTransaction(transaction)
        }
    }

    suspend fun deleteTransaction(transaction: TransactionEntity): Int {
        return withContext(Dispatchers.IO) {
            transactionDao.deleteTransaction(transaction)
        }
    }
}
