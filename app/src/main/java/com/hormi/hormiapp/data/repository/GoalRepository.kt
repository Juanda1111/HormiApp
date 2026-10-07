package com.hormi.hormiapp.data.repository

import com.hormi.hormiapp.data.local.dao.GoalDao
import com.hormi.hormiapp.data.local.entity.GoalEntity
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

/** Metas de la cuenta activa: cada cuenta solo ve y modifica las suyas. */
@Singleton
class GoalRepository @Inject constructor(
    private val goalDao: GoalDao,
    private val preferences: UserPreferencesRepository
) {
    @OptIn(ExperimentalCoroutinesApi::class)
    fun getAllGoals(): Flow<List<GoalEntity>> =
        preferences.currentUserId.flatMapLatest { id ->
            if (id == null) flowOf(emptyList()) else goalDao.getAllGoals(id)
        }

    suspend fun insertGoal(goal: GoalEntity): Long {
        val userId = preferences.currentUserId.first() ?: return -1
        return withContext(Dispatchers.IO) {
            goalDao.insertGoal(goal.copy(userId = userId))
        }
    }

    suspend fun updateGoal(goal: GoalEntity): Int {
        val userId = preferences.currentUserId.first() ?: return 0
        return withContext(Dispatchers.IO) {
            goalDao.updateGoal(goal.copy(userId = userId))
        }
    }

    suspend fun deleteGoal(goal: GoalEntity): Int {
        return withContext(Dispatchers.IO) {
            goalDao.deleteGoal(goal)
        }
    }

    suspend fun deleteAllForUser(userId: String): Int {
        return withContext(Dispatchers.IO) {
            goalDao.deleteAllForUser(userId)
        }
    }
}
