package com.hormi.hormiapp.data.local

import androidx.room.Database
import androidx.room.RoomDatabase
import com.hormi.hormiapp.data.local.dao.GoalDao
import com.hormi.hormiapp.data.local.dao.TransactionDao
import com.hormi.hormiapp.data.local.entity.GoalEntity
import com.hormi.hormiapp.data.local.entity.TransactionEntity

@Database(
    entities = [TransactionEntity::class, GoalEntity::class],
    version = 3,
    exportSchema = false
)
abstract class HormiAppDatabase : RoomDatabase() {
    abstract val transactionDao: TransactionDao
    abstract val goalDao: GoalDao
}
