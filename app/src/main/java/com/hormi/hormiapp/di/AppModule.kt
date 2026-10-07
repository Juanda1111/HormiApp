package com.hormi.hormiapp.di

import android.content.Context
import androidx.room.Room
import com.hormi.hormiapp.data.local.HormiAppDatabase
import com.hormi.hormiapp.data.local.dao.GoalDao
import com.hormi.hormiapp.data.local.dao.TransactionDao
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object AppModule {

    @Provides
    @Singleton
    fun provideHormiAppDatabase(
        @ApplicationContext context: Context
    ): HormiAppDatabase {
        return Room.databaseBuilder(
            context,
            HormiAppDatabase::class.java,
            "hormiapp_db"
        )
        .fallbackToDestructiveMigration()
        .build()
    }

    @Provides
    @Singleton
    fun provideTransactionDao(
        database: HormiAppDatabase
    ): TransactionDao {
        return database.transactionDao
    }

    @Provides
    @Singleton
    fun provideGoalDao(
        database: HormiAppDatabase
    ): GoalDao {
        return database.goalDao
    }
}
