package com.hormi.hormiapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extensión para inicializar DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hormiapp_prefs")

@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private object PreferencesKeys {
        val USER_NAME = stringPreferencesKey("user_name")
        val USER_PIN = stringPreferencesKey("user_pin")
        val SECURITY_ANSWER = stringPreferencesKey("security_answer")
        val HAS_COMPLETED_ONBOARDING = booleanPreferencesKey("has_completed_onboarding")
        val MONTHLY_INCOME = stringPreferencesKey("monthly_income")
    }

    val userName: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.USER_NAME] }

    val userPin: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.USER_PIN] }

    val securityAnswer: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.SECURITY_ANSWER] }

    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
    }

    val monthlyIncome: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.MONTHLY_INCOME] }

    suspend fun saveUserData(name: String, pin: String, answer: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.USER_PIN] = pin
            preferences[PreferencesKeys.SECURITY_ANSWER] = answer
        }
    }

    suspend fun saveUserPinOnly(pin: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_PIN] = pin
        }
    }

    suspend fun setOnboardingCompleted(income: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] = true
            preferences[PreferencesKeys.MONTHLY_INCOME] = income
        }
    }
}
