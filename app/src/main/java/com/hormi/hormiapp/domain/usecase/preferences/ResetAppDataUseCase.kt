package com.hormi.hormiapp.domain.usecase.preferences

import android.content.Context
import com.hormi.hormiapp.data.local.HormiAppDatabase
import com.hormi.hormiapp.data.preferences.UserPreferencesRepository
import com.hormi.hormiapp.domain.usecase.transactions.InjectDemoDataUseCase
import com.hormi.hormiapp.reminder.ReminderScheduler
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import javax.inject.Inject

/** Borra gastos, ingresos, metas, preferencias (incluida la cuenta) y el recordatorio. */
class ClearAllDataUseCase @Inject constructor(
    @ApplicationContext private val context: Context,
    private val database: HormiAppDatabase,
    private val preferences: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        ReminderScheduler.cancel(context)
        withContext(Dispatchers.IO) { database.clearAllTables() }
        preferences.clearAll()
    }
}

/**
 * Crea la cuenta de demostración: borra lo que haya y carga una cuenta "Demo" con datos de ejemplo.
 * Solo se usa desde el botón "Probar con usuario demo"; una cuenta nueva siempre empieza vacía.
 */
class StartDemoUseCase @Inject constructor(
    private val clearAllData: ClearAllDataUseCase,
    private val preferences: UserPreferencesRepository,
    private val injectDemoData: InjectDemoDataUseCase
) {
    suspend operator fun invoke() {
        clearAllData()
        preferences.saveUserData(name = DEMO_NAME, pin = DEMO_PIN, answer = DEMO_ANSWER)
        preferences.setOnboardingCompleted(DEMO_INCOME)
        injectDemoData()
    }

    companion object {
        const val DEMO_NAME = "Demo"
        const val DEMO_PIN = "1234"
        private const val DEMO_ANSWER = "demo"
        private const val DEMO_INCOME = "2000000"
    }
}
