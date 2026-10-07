package com.hormi.hormiapp.data.repository

import com.hormi.hormiapp.data.local.dao.TransactionDao
import com.hormi.hormiapp.data.local.entity.TransactionEntity
import com.hormi.hormiapp.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

/** Transacciones de la cuenta activa: cada cuenta solo ve y modifica las suyas. */
@Singleton
class TransactionRepository @Inject constructor(
    private val transactionDao: TransactionDao,
    private val preferences: UserPreferencesRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllTransactions(): Flow<List<TransactionEntity>> =
        preferences.currentUserId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else transactionDao.getAllTransactions(id)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>> =
        preferences.currentUserId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else transactionDao.getTransactionsByType(id, type)
        }

    @OptIn(ExperimentalCoroutinesApi::class)
    fun getTotalAntExpenses(): Flow<Double?> =
        preferences.currentUserId.flatMapLatest { id ->
            if (id == null) flowOf(null) else transactionDao.getTotalAntExpenses(id)
        }

    suspend fun insertTransaction(transaction: TransactionEntity): Long {
        val userId = preferences.currentUserId.first() ?: return -1
        return withContext(Dispatchers.IO) {
            transactionDao.insertTransaction(transaction.copy(userId = userId))
        }
    }

    suspend fun updateTransaction(transaction: TransactionEntity): Int {
        val userId = preferences.currentUserId.first() ?: return 0
        return withContext(Dispatchers.IO) {
            transactionDao.updateTransaction(transaction.copy(userId = userId))
        }
    }

    suspend fun deleteTransaction(transaction: TransactionEntity): Int {
        return withContext(Dispatchers.IO) {
            transactionDao.deleteTransaction(transaction)
        }
    }

    suspend fun deleteAllForUser(userId: String): Int {
        return withContext(Dispatchers.IO) {
            transactionDao.deleteAllForUser(userId)
        }
    }
}
