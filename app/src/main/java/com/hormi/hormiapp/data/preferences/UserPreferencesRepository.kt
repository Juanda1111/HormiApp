package com.hormi.hormiapp.data.preferences

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.MutablePreferences
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.doublePreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.longPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.core.stringSetPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.hormi.hormiapp.reminder.ReminderScheduler
import com.hormi.hormiapp.util.AccountId
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

// Extensión para inicializar DataStore
val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "hormiapp_prefs")

enum class LoginResult { OK, NOT_FOUND, WRONG_PIN }

/**
 * Ajustes del dispositivo y de cada cuenta.
 *
 * Un mismo celular puede tener varias cuentas. Cada cuenta guarda sus ajustes con el prefijo
 * "u:<id>:" y [CURRENT_USER] indica la cuenta activa (la última que inició sesión). Todos los
 * valores públicos (userName, currency, theme...) se refieren siempre a la cuenta activa.
 */
@Singleton
class UserPreferencesRepository @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private companion object {
        val ACCOUNTS = stringSetPreferencesKey("accounts")
        val CURRENT_USER = stringPreferencesKey("current_user")

        const val NAME = "user_name"
        const val PIN = "user_pin"
        const val ANSWER = "security_answer"
        const val ONBOARDING = "has_completed_onboarding"
        const val INCOME = "monthly_income"
        const val REGISTERED_AT = "registered_at"
        const val CURRENCY = "currency"
        const val REMINDER_ENABLED = "reminder_enabled"
        const val REMINDER_TIME = "reminder_time"
        const val THEME = "theme"
        const val THRESHOLD = "ant_expense_threshold"

        fun prefix(id: String) = "u:$id:"
        fun str(id: String, field: String) = stringPreferencesKey(prefix(id) + field)
        fun bool(id: String, field: String) = booleanPreferencesKey(prefix(id) + field)
        fun lng(id: String, field: String) = longPreferencesKey(prefix(id) + field)
        fun dbl(id: String, field: String) = doublePreferencesKey(prefix(id) + field)
    }

    private val data get() = context.dataStore.data

    /** Id de la cuenta activa, o null si todavía no hay ninguna. */
    val currentUserId: Flow<String?> = data.map { it[CURRENT_USER] }.distinctUntilChanged()

    private fun <T> perUser(default: T, read: (Preferences, String) -> T?): Flow<T> =
        data.map { p -> p[CURRENT_USER]?.let { id -> read(p, id) } ?: default }.distinctUntilChanged()

    val userName: Flow<String?> = perUser<String?>(null) { p, id -> p[str(id, NAME)] }

    val userPin: Flow<String?> = perUser<String?>(null) { p, id -> p[str(id, PIN)] }

    val securityAnswer: Flow<String?> = perUser<String?>(null) { p, id -> p[str(id, ANSWER)] }

    val hasCompletedOnboarding: Flow<Boolean> = perUser(false) { p, id -> p[bool(id, ONBOARDING)] }

    val monthlyIncome: Flow<String?> = perUser<String?>(null) { p, id -> p[str(id, INCOME)] }

    val antExpenseThreshold: Flow<Double> = perUser(15000.0) { p, id -> p[dbl(id, THRESHOLD)] }

    /** Momento en que se creó la cuenta (millis), o null si no se guardó. */
    val registeredAt: Flow<Long?> = perUser<Long?>(null) { p, id -> p[lng(id, REGISTERED_AT)] }

    val currency: Flow<String> = perUser("COP $") { p, id -> p[str(id, CURRENCY)] }

    val reminderEnabled: Flow<Boolean> = perUser(false) { p, id -> p[bool(id, REMINDER_ENABLED)] }

    val reminderTime: Flow<String> = perUser("20:00") { p, id -> p[str(id, REMINDER_TIME)] }

    val theme: Flow<String> = perUser("Sistema") { p, id -> p[str(id, THEME)] }

    // ---------------- Cuentas ----------------

    suspend fun hasAccounts(): Boolean = data.first()[ACCOUNTS].orEmpty().isNotEmpty()

    suspend fun accountExists(name: String): Boolean =
        AccountId.normalize(name) in data.first()[ACCOUNTS].orEmpty()

    /** Crea la cuenta y la deja como cuenta activa. No toca las demás cuentas. */
    suspend fun createAccount(name: String, pin: String, answer: String) {
        val id = AccountId.normalize(name)
        context.dataStore.edit { p ->
            p[ACCOUNTS] = p[ACCOUNTS].orEmpty() + id
            p[str(id, NAME)] = name.trim().replace(Regex("\\s+"), " ")
            p[str(id, PIN)] = pin
            p[str(id, ANSWER)] = answer
            p[lng(id, REGISTERED_AT)] = System.currentTimeMillis()
            p[CURRENT_USER] = id
        }
        applyReminder()
    }

    /** Valida nombre y PIN; si son correctos, la cuenta pasa a ser la activa. */
    suspend fun login(name: String, pin: String): LoginResult {
        val id = AccountId.normalize(name)
        var result = LoginResult.NOT_FOUND
        context.dataStore.edit { p ->
            if (id in p[ACCOUNTS].orEmpty()) {
                if (p[str(id, PIN)] == pin) {
                    p[CURRENT_USER] = id
                    result = LoginResult.OK
                } else {
                    result = LoginResult.WRONG_PIN
                }
            }
        }
        if (result == LoginResult.OK) applyReminder()
        return result
    }

    suspend fun getSecurityAnswer(name: String): String? =
        data.first()[str(AccountId.normalize(name), ANSWER)]

    suspend fun resetPin(name: String, newPin: String) {
        val id = AccountId.normalize(name)
        context.dataStore.edit { p ->
            if (id in p[ACCOUNTS].orEmpty()) p[str(id, PIN)] = newPin
        }
    }

    /** Elimina los ajustes de una cuenta y la saca de la lista. Los datos en Room se borran aparte. */
    suspend fun removeAccount(id: String) {
        context.dataStore.edit { p ->
            val prefix = prefix(id)
            p.asMap().keys.filter { it.name.startsWith(prefix) }.forEach { p.remove(it) }
            p[ACCOUNTS] = p[ACCOUNTS].orEmpty() - id
            if (p[CURRENT_USER] == id) p.remove(CURRENT_USER)
        }
        applyReminder()
    }

    // ---------------- Ajustes de la cuenta activa ----------------

    private suspend fun editCurrent(block: (MutablePreferences, String) -> Unit) {
        context.dataStore.edit { p -> p[CURRENT_USER]?.let { block(p, it) } }
    }

    suspend fun setOnboardingCompleted(income: String) = editCurrent { p, id ->
        p[bool(id, ONBOARDING)] = true
        p[str(id, INCOME)] = income
    }

    suspend fun saveMonthlyIncome(income: String) = editCurrent { p, id -> p[str(id, INCOME)] = income }

    suspend fun updateAntExpenseThreshold(threshold: Double) = editCurrent { p, id -> p[dbl(id, THRESHOLD)] = threshold }

    suspend fun updateCurrency(currency: String) = editCurrent { p, id -> p[str(id, CURRENCY)] = currency }

    suspend fun updateReminder(enabled: Boolean, time: String) = editCurrent { p, id ->
        p[bool(id, REMINDER_ENABLED)] = enabled
        p[str(id, REMINDER_TIME)] = time
    }

    suspend fun updateTheme(theme: String) = editCurrent { p, id -> p[str(id, THEME)] = theme }

    /** Para cuentas que no guardaron la fecha de creación: la fija la primera vez que se consulta. */
    suspend fun ensureRegisteredAt() = editCurrent { p, id ->
        if (p[lng(id, REGISTERED_AT)] == null) p[lng(id, REGISTERED_AT)] = System.currentTimeMillis()
    }

    /** El recordatorio es uno por dispositivo: refleja el ajuste de la cuenta activa. */
    private suspend fun applyReminder() {
        val p = data.first()
        val id = p[CURRENT_USER]
        val enabled = id?.let { p[bool(it, REMINDER_ENABLED)] } ?: false
        val time = id?.let { p[str(it, REMINDER_TIME)] } ?: "20:00"
        if (enabled) ReminderScheduler.schedule(context, time) else ReminderScheduler.cancel(context)
    }
}
