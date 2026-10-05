package com.hormi.hormiapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hormi.hormiapp.data.local.dao.TransactionDao
import com.hormi.hormiapp.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class],
    version = 1,
    exportSchema = false
)
abstract class HormiAppDatabase : RoomDatabase() {
    abstract val transactionDao: TransactionDao
}
