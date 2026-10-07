package com.hormi.hormiapp.domain.usecase.transactions

import com.hormi.hormiapp.data.local.entity.GoalEntity
import com.hormi.hormiapp.data.local.entity.TransactionEntity
import com.hormi.hormiapp.data.repository.GoalRepository
import com.hormi.hormiapp.data.repository.TransactionRepository
import javax.inject.Inject
import kotlin.random.Random

/** Carga datos de ejemplo repartidos en las últimas 5 semanas para ver Análisis, Gastos y Metas con información. */
class InjectDemoDataUseCase @Inject constructor(
    private val repository: TransactionRepository,
    private val goalRepository: GoalRepository
) {
    private data class Template(val category: String, val description: String, val amount: Double)

    private val antTemplates = listOf(
        Template("Café y snacks", "Tinto antes de clase", 2500.0),
        Template("Café y snacks", "Snack en la tarde", 4000.0),
        Template("Café y snacks", "Empanada y gaseosa", 5000.0),
        Template("Café y snacks", "Galletas", 3500.0),
        Template("Transporte", "Pasaje bus extra", 3200.0),
        Template("Transporte", "Pasaje de afán", 3200.0),
        Template("Domicilios", "Costo de envío", 3900.0),
        Template("Domicilios", "Propina domicilio", 2000.0)
    )

    private val bigTemplates = listOf(
        Template("Comida", "Almuerzo", 14000.0 + 3000.0),
        Template("Salidas", "Cine con amigos", 45000.0),
        Template("Suscripciones", "Plan de música", 24900.0),
        Template("Universidad", "Fotocopias y libros", 38000.0),
        Template("Transporte", "Recarga de tarjeta", 30000.0)
    )

    suspend operator fun invoke() {
        val now = System.currentTimeMillis()
        val oneDay = 86_400_000L
        val random = Random(7)

        // 35 días hacia atrás: 1 a 3 gastos hormiga por día y algún gasto grande cada pocos días
        for (daysAgo in 0 until 35) {
            val count = if (daysAgo <= 1) 2 else random.nextInt(1, 4)
            repeat(count) {
                val t = antTemplates[random.nextInt(antTemplates.size)]
                // Distintas horas del día para que el orden se vea natural
                val timestamp = now - daysAgo * oneDay - random.nextLong(0, 6 * 3_600_000L)
                repository.insertTransaction(
                    TransactionEntity(
                        amount = t.amount, type = "EXPENSE", category = t.category,
                        description = t.description, dateTimestamp = timestamp, isAntExpense = true
                    )
                )
            }
            if (daysAgo % 4 == 1) {
                val t = bigTemplates[random.nextInt(bigTemplates.size)]
                repository.insertTransaction(
                    TransactionEntity(
                        amount = t.amount, type = "EXPENSE", category = t.category,
                        description = t.description, dateTimestamp = now - daysAgo * oneDay - 3_600_000L,
                        isAntExpense = false
                    )
                )
            }
        }

        // Ingreso extra
        repository.insertTransaction(
            TransactionEntity(
                amount = 50000.0, type = "INCOME", category = "Ingreso extra", description = "Pago deuda",
                dateTimestamp = now - 2 * oneDay, isAntExpense = false
            )
        )

        val demoGoals = listOf(
            GoalEntity(name = "Portátil nuevo", targetAmount = 3000000.0, currentAmount = 1200000.0, iconName = "Laptop"),
            GoalEntity(name = "Viaje a Santa Marta", targetAmount = 1200000.0, currentAmount = 900000.0, iconName = "Airplane"),
            GoalEntity(name = "Audífonos", targetAmount = 250000.0, currentAmount = 250000.0, iconName = "Headphones")
        )
        demoGoals.forEach { goal -> goalRepository.insertGoal(goal) }
    }
}
