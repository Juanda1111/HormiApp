package com.hormi.hormiapp.domain.usecase.transactions

import com.hormi.hormiapp.data.local.entity.GoalEntity
import com.hormi.hormiapp.data.local.entity.TransactionEntity
import com.hormi.hormiapp.data.repository.GoalRepository
import com.hormi.hormiapp.data.repository.TransactionRepository
import javax.inject.Inject

class InjectDemoDataUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val goalRepository: GoalRepository
) {
    suspend operator fun invoke() {
        val now = System.currentTimeMillis()
        val oneDay = 86400000L

        val demoTransactions = listOf(
            // Café y snacks (Total 21500 en 6 gastos)
            TransactionEntity(amount = 2500.0, type = "EXPENSE", category = "Café y snacks", description = "Tinto antes de clase", dateTimestamp = now - (oneDay * 0), isAntExpense = true),
            TransactionEntity(amount = 4000.0, type = "EXPENSE", category = "Café y snacks", description = "Snack en la tarde", dateTimestamp = now - (oneDay * 0), isAntExpense = true),
            TransactionEntity(amount = 3000.0, type = "EXPENSE", category = "Café y snacks", description = "Café de máquina", dateTimestamp = now - (oneDay * 1), isAntExpense = true),
            TransactionEntity(amount = 5000.0, type = "EXPENSE", category = "Café y snacks", description = "Empanada y gaseosa", dateTimestamp = now - (oneDay * 2), isAntExpense = true),
            TransactionEntity(amount = 4500.0, type = "EXPENSE", category = "Café y snacks", description = "Galletas", dateTimestamp = now - (oneDay * 3), isAntExpense = true),
            TransactionEntity(amount = 2500.0, type = "EXPENSE", category = "Café y snacks", description = "Tinto", dateTimestamp = now - (oneDay * 4), isAntExpense = true),
            
            // Transporte (Total 6400 en 2 gastos)
            TransactionEntity(amount = 3200.0, type = "EXPENSE", category = "Transporte", description = "Pasaje bus extra", dateTimestamp = now - (oneDay * 1), isAntExpense = true),
            TransactionEntity(amount = 3200.0, type = "EXPENSE", category = "Transporte", description = "Pasaje de afán", dateTimestamp = now - (oneDay * 2), isAntExpense = true),
            
            // Domicilios (Total 3900 en 1 gasto)
            TransactionEntity(amount = 3900.0, type = "EXPENSE", category = "Domicilios", description = "Costo de envío", dateTimestamp = now - (oneDay * 3), isAntExpense = true),
            
            // Gastos no hormiga para bulto
            TransactionEntity(amount = 45000.0, type = "EXPENSE", category = "Salidas", description = "Cine con amigos", dateTimestamp = now - (oneDay * 1), isAntExpense = false),
            TransactionEntity(amount = 12000.0, type = "EXPENSE", category = "Alimentación", description = "Almuerzo", dateTimestamp = now - (oneDay * 0), isAntExpense = false),
            
            // Ingreso extra
            TransactionEntity(amount = 50000.0, type = "INCOME", category = "Transferencia", description = "Pago deuda", dateTimestamp = now - (oneDay * 2), isAntExpense = false)
        )

        demoTransactions.forEach { transaction ->
            repository.insertTransaction(transaction)
        }

        val demoGoals = listOf(
            GoalEntity(name = "Portátil nuevo", targetAmount = 3000000.0, currentAmount = 1200000.0, iconName = "Laptop"),
            GoalEntity(name = "Viaje a Santa Marta", targetAmount = 1200000.0, currentAmount = 900000.0, iconName = "Airplane"),
            GoalEntity(name = "Audífonos", targetAmount = 250000.0, currentAmount = 250000.0, iconName = "Headphones")
        )

        demoGoals.forEach { goal ->
            goalRepository.insertGoal(goal)
        }
    }
}
