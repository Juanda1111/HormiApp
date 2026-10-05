package com.hormi.hormiapp.domain.usecase.transactions

import com.hormi.hormiapp.data.local.entity.TransactionEntity
import com.hormi.hormiapp.data.repository.TransactionRepository
import javax.inject.Inject

class InjectDemoDataUseCase @Inject constructor(
    private val repository: TransactionRepository
) {
    suspend operator fun invoke() {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L

        val demoTransactions = listOf(
            TransactionEntity(
                amount = 2500.0,
                type = "EXPENSE",
                category = "Hormiga",
                description = "Tinto de la mañana",
                dateTimestamp = now - (oneDay * 0), // Hoy
                isAntExpense = true
            ),
            TransactionEntity(
                amount = 12000.0,
                type = "EXPENSE",
                category = "Alimentación",
                description = "Almuerzo casero",
                dateTimestamp = now - (oneDay * 0), // Hoy
                isAntExpense = false
            ),
            TransactionEntity(
                amount = 5000.0,
                type = "EXPENSE",
                category = "Hormiga",
                description = "Snacks en la tarde",
                dateTimestamp = now - (oneDay * 1), // Ayer
                isAntExpense = true
            ),
            TransactionEntity(
                amount = 45000.0,
                type = "EXPENSE",
                category = "Entretenimiento",
                description = "Cine con amigos",
                dateTimestamp = now - (oneDay * 2), // Hace 2 días
                isAntExpense = false
            ),
            TransactionEntity(
                amount = 3500.0,
                type = "EXPENSE",
                category = "Hormiga",
                description = "Golosina",
                dateTimestamp = now - (oneDay * 3), // Hace 3 días
                isAntExpense = true
            ),
            TransactionEntity(
                amount = 50000.0,
                type = "INCOME",
                category = "Regalo",
                description = "Dinero de cumpleaños",
                dateTimestamp = now - (oneDay * 4), // Hace 4 días
                isAntExpense = false
            )
        )

        demoTransactions.forEach { transaction ->
            repository.insertTransaction(transaction)
        }
    }
}
