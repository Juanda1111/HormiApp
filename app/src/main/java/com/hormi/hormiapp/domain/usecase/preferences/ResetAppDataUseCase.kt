package com.hormi.hormiapp.domain.usecase.preferences

import com.hormi.hormiapp.data.preferences.UserPreferencesRepository
import com.hormi.hormiapp.data.repository.GoalRepository
import com.hormi.hormiapp.data.repository.TransactionRepository
import com.hormi.hormiapp.domain.usecase.transactions.InjectDemoDataUseCase
import com.hormi.hormiapp.util.AccountId
import kotlinx.coroutines.flow.first
import javax.inject.Inject

/** Elimina una cuenta con todos sus datos (gastos, ingresos, metas y ajustes). Las demás cuentas no se tocan. */
class DeleteAccountUseCase @Inject constructor(
    private val preferences: UserPreferencesRepository,
    private val transactions: TransactionRepository,
    private val goals: GoalRepository
) {
    suspend operator fun invoke(userId: String) {
        transactions.deleteAllForUser(userId)
        goals.deleteAllForUser(userId)
        preferences.removeAccount(userId)
    }

    suspend fun deleteCurrent() {
        preferences.currentUserId.first()?.let { invoke(it) }
    }
}

/**
 * Crea (o reinicia) la cuenta de demostración con datos de ejemplo y la deja activa.
 * No afecta a las demás cuentas del dispositivo.
 */
class StartDemoUseCase @Inject constructor(
    private val preferences: UserPreferencesRepository,
    private val deleteAccount: DeleteAccountUseCase,
    private val injectDemoData: InjectDemoDataUseCase
) {
    suspend operator fun invoke() {
        if (preferences.accountExists(DEMO_NAME)) deleteAccount(AccountId.normalize(DEMO_NAME))
        preferences.createAccount(name = DEMO_NAME, pin = DEMO_PIN, answer = DEMO_ANSWER)
        preferences.setOnboardingCompleted(DEMO_INCOME)
        injectDemoData()
    }

    companion object {
        const val DEMO_NAME = AccountId.DEMO_NAME
        const val DEMO_PIN = "1234"
        private const val DEMO_ANSWER = "demo"
        private const val DEMO_INCOME = "2000000"
    }
}
