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
    @Query("SELECT * FROM transactions ORDER BY dateTimestamp DESC")
    fun getAllTransactions(): Flow<List<TransactionEntity>>

    // Obtener transacciones por tipo (INCOME / EXPENSE)
    @Query("SELECT * FROM transactions WHERE type = :type ORDER BY dateTimestamp DESC")
    fun getTransactionsByType(type: String): Flow<List<TransactionEntity>>

    // Sumar todos los gastos hormiga
    @Query("SELECT SUM(amount) FROM transactions WHERE isAntExpense = 1")
    fun getTotalAntExpenses(): Flow<Double?>
}
