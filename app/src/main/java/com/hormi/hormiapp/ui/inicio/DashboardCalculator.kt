package com.hormi.hormiapp.ui.inicio

import com.hormi.hormiapp.data.local.entity.TransactionEntity
import com.hormi.hormiapp.ui.analisis.AnalysisCalculator
import java.util.Calendar

/**
 * Cálculos de la pantalla de Inicio a partir de las transacciones reales.
 *
 * - Saldo, ingresos y gastos son del mes actual (el ingreso mensual se repite cada mes).
 * - El presupuesto semanal es el 25 % del ingreso mensual (igual que en Configuración)
 *   y se compara con lo gastado de lunes a domingo de la semana actual.
 * - "Últimos gastos" muestra solo gastos, los más recientes primero.
 */
object DashboardCalculator {

    private const val WEEKLY_BUDGET_FRACTION = 0.25
    private const val RECENT_COUNT = 5

    private fun monthStart(now: Long, monthOffset: Int = 0): Long = Calendar.getInstance().apply {
        timeInMillis = now
        set(Calendar.DAY_OF_MONTH, 1)
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
        add(Calendar.MONTH, monthOffset)
    }.timeInMillis

    fun calculate(
        baseIncome: Double,
        transactions: List<TransactionEntity>,
        now: Long = System.currentTimeMillis()
    ): InicioUiState {
        val thisMonth = monthStart(now)
        val nextMonth = monthStart(now, 1)
        val monthTx = transactions.filter { it.dateTimestamp in thisMonth until nextMonth }

        val monthExpenses = monthTx.filter { it.type == "EXPENSE" }
        val totalExpenses = monthExpenses.sumOf { it.amount }
        val extraIncome = monthTx.filter { it.type == "INCOME" }.sumOf { it.amount }
        val totalIncome = baseIncome + extraIncome

        // Mayor categoría de gasto hormiga del mes
        val topAnt = monthExpenses.filter { it.isAntExpense }
            .groupBy { it.category }
            .maxByOrNull { entry -> entry.value.sumOf { it.amount } }

        // Presupuesto semanal: gastos de lunes a domingo de esta semana
        val weekStart = AnalysisCalculator.startOfWeek(now)
        val nextWeek = AnalysisCalculator.startOfWeek(now + 7L * 24 * 60 * 60 * 1000)
        val weeklySpent = transactions
            .filter { it.type == "EXPENSE" && it.dateTimestamp in weekStart until nextWeek }
            .sumOf { it.amount }
        val weeklyBudget = baseIncome * WEEKLY_BUDGET_FRACTION
        val weeklyPercent = if (weeklyBudget > 0) (weeklySpent / weeklyBudget).coerceAtMost(1.0) else 0.0

        return InicioUiState(
            availableBalance = totalIncome - totalExpenses,
            totalIncome = totalIncome,
            totalExpenses = totalExpenses,
            weeklyBudget = weeklyBudget,
            weeklySpent = weeklySpent,
            weeklyRemaining = (weeklyBudget - weeklySpent).coerceAtLeast(0.0),
            weeklyPercent = weeklyPercent.toFloat(),
            highestAntExpenseCategory = topAnt?.key ?: "Ninguno",
            highestAntExpenseCount = topAnt?.value?.size ?: 0,
            highestAntExpenseTotal = topAnt?.value?.sumOf { it.amount } ?: 0.0,
            recentTransactions = transactions
                .filter { it.type == "EXPENSE" }
                .sortedByDescending { it.dateTimestamp }
                .take(RECENT_COUNT)
        )
    }
}
