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
        val REGISTERED_AT = androidx.datastore.preferences.core.longPreferencesKey("registered_at")
        val CURRENCY = stringPreferencesKey("currency")
        val REMINDER_ENABLED = booleanPreferencesKey("reminder_enabled")
        val REMINDER_TIME = stringPreferencesKey("reminder_time")
        val THEME = stringPreferencesKey("theme")
        val ANT_EXPENSE_THRESHOLD = androidx.datastore.preferences.core.doublePreferencesKey("ant_expense_threshold")
    }

    val userName: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.USER_NAME] }

    val userPin: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.USER_PIN] }

    val securityAnswer: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.SECURITY_ANSWER] }

    val hasCompletedOnboarding: Flow<Boolean> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.HAS_COMPLETED_ONBOARDING] ?: false
    }

    val monthlyIncome: Flow<String?> = context.dataStore.data.map { it[PreferencesKeys.MONTHLY_INCOME] }

    val antExpenseThreshold: Flow<Double> = context.dataStore.data.map { preferences ->
        preferences[PreferencesKeys.ANT_EXPENSE_THRESHOLD] ?: 15000.0
    }

    /** Momento en que se creó la cuenta (millis), o null en cuentas creadas antes de guardar este dato. */
    val registeredAt: Flow<Long?> = context.dataStore.data.map { it[PreferencesKeys.REGISTERED_AT] }

    val currency: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.CURRENCY] ?: "COP $" }

    val reminderEnabled: Flow<Boolean> = context.dataStore.data.map { it[PreferencesKeys.REMINDER_ENABLED] ?: false }

    val reminderTime: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.REMINDER_TIME] ?: "20:00" }

    val theme: Flow<String> = context.dataStore.data.map { it[PreferencesKeys.THEME] ?: "Sistema" }

    suspend fun saveUserData(name: String, pin: String, answer: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_NAME] = name
            preferences[PreferencesKeys.USER_PIN] = pin
            preferences[PreferencesKeys.SECURITY_ANSWER] = answer
            preferences[PreferencesKeys.REGISTERED_AT] = System.currentTimeMillis()
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

    suspend fun updateAntExpenseThreshold(threshold: Double) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ANT_EXPENSE_THRESHOLD] = threshold
        }
    }

    suspend fun saveMonthlyIncome(income: String) {
        context.dataStore.edit { it[PreferencesKeys.MONTHLY_INCOME] = income }
    }

    suspend fun updateCurrency(currency: String) {
        context.dataStore.edit { it[PreferencesKeys.CURRENCY] = currency }
    }

    suspend fun updateReminder(enabled: Boolean, time: String) {
        context.dataStore.edit {
            it[PreferencesKeys.REMINDER_ENABLED] = enabled
            it[PreferencesKeys.REMINDER_TIME] = time
        }
    }

    suspend fun updateTheme(theme: String) {
        context.dataStore.edit { it[PreferencesKeys.THEME] = theme }
    }

    suspend fun clearAll() {
        context.dataStore.edit { it.clear() }
    }

    /** Para cuentas anteriores a este dato: fija la fecha la primera vez que se consulta. */
    suspend fun ensureRegisteredAt() {
        context.dataStore.edit {
            if (it[PreferencesKeys.REGISTERED_AT] == null) it[PreferencesKeys.REGISTERED_AT] = System.currentTimeMillis()
        }
    }
}
