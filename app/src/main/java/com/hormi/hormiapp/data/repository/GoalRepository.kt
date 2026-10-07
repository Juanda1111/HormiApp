package com.hormi.hormiapp.data.repository

import com.hormi.hormiapp.data.local.dao.GoalDao
import com.hormi.hormiapp.data.local.entity.GoalEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao
) {
    fun getAllGoals(): Flow<List<GoalEntity>> {
        return goalDao.getAllGoals()
    }

    suspend fun insertGoal(goal: GoalEntity): Long {
        return withContext(Dispatchers.IO) {
            goalDao.insertGoal(goal)
        }
    }

    suspend fun updateGoal(goal: GoalEntity): Int {
        return withContext(Dispatchers.IO) {
            goalDao.updateGoal(goal)
        }
    }

    suspend fun deleteGoal(goal: GoalEntity): Int {
        return withContext(Dispatchers.IO) {
            goalDao.deleteGoal(goal)
        }
    }
}
