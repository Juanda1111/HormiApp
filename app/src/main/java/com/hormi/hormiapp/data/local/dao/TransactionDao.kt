package com.hormi.hormiapp.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.hormi.hormiapp.data.local.entity.TransactionEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TransactionDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    fun insertTransaction(transaction: TransactionEntity): Long

    @Update
    fun updateTransaction(transaction: TransactionEntity): Int

    @Delete
    fun deleteTransaction(transaction: TransactionEntity): Int

    // Obtener todas las transacciones ordenadas por fecha (más recientes primero)
    @Query("SELECT * FROM transactions WHERE userId = :userId ORDER BY dateTimestamp DESC")
    fun getAllTransactions(userId: String): Flow<List<TransactionEntity>>

    // Obtener transacciones por tipo (INCOME / EXPENSE)
    @Query("SELECT * FROM transactions WHERE userId = :userId AND type = :type ORDER BY dateTimestamp DESC")
    fun getTransactionsByType(userId: String, type: String): Flow<List<TransactionEntity>>

    // Sumar todos los gastos hormiga
    @Query("SELECT SUM(amount) FROM transactions WHERE userId = :userId AND isAntExpense = 1")
    fun getTotalAntExpenses(userId: String): Flow<Double?>

    @Query("DELETE FROM transactions WHERE userId = :userId")
    fun deleteAllForUser(userId: String): Int
}
