package com.hormi.hormiapp.ui.analisis

import com.hormi.hormiapp.data.local.entity.TransactionEntity
import java.util.Calendar

/**
 * Cálculos de la pantalla de Análisis a partir de las transacciones reales.
 *
 * - "Esta semana" va de lunes 00:00 a domingo 23:59.
 * - La semana anterior se compara solo con gastos reales; si no hubo gastos hormiga, no hay comparación.
 * - Las proyecciones usan el promedio diario de los últimos 28 días, no un total histórico.
 */
object AnalysisCalculator {

    private const val DAY_MS = 24L * 60 * 60 * 1000
    private const val PROJECTION_WINDOW_DAYS = 28
    private const val MIN_PROJECTION_DAYS = 7
    private const val DAYS_PER_MONTH = 30

    /** Lunes 00:00 de la semana que contiene a [now]. */
    fun startOfWeek(now: Long): Long {
        val cal = Calendar.getInstance().apply {
            timeInMillis = now
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        val daysSinceMonday = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
        cal.add(Calendar.DAY_OF_YEAR, -daysSinceMonday)
        return cal.timeInMillis
    }

    private fun startOfDay(timestamp: Long): Long = Calendar.getInstance().apply {
        timeInMillis = timestamp
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }.timeInMillis

    fun startOfPreviousWeek(now: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = startOfWeek(now) }
        cal.add(Calendar.DAY_OF_YEAR, -7)
        return cal.timeInMillis
    }

    private fun startOfNextWeek(now: Long): Long {
        val cal = Calendar.getInstance().apply { timeInMillis = startOfWeek(now) }
        cal.add(Calendar.DAY_OF_YEAR, 7)
        return cal.timeInMillis
    }

    fun calculate(transactions: List<TransactionEntity>, now: Long = System.currentTimeMillis()): AnalisisUiState {
        val expenses = transactions.filter { it.type == "EXPENSE" }
        val weekStart = startOfWeek(now)
        val nextWeekStart = startOfNextWeek(now)
        val previousWeekStart = startOfPreviousWeek(now)

        val thisWeek = expenses.filter { it.dateTimestamp in weekStart until nextWeekStart }
        val previousWeek = expenses.filter { it.dateTimestamp in previousWeekStart until weekStart }

        val totalAnt = thisWeek.filter { it.isAntExpense }.sumOf { it.amount }
        val previousAnt = previousWeek.filter { it.isAntExpense }.sumOf { it.amount }
        val percentChange = if (previousAnt > 0) ((totalAnt - previousAnt) / previousAnt).toFloat() else 0f

        val totalThisWeek = thisWeek.sumOf { it.amount }
        val stats = thisWeek.groupBy { it.category }.map { (category, list) ->
            val sum = list.sumOf { it.amount }
            CategoryStat(
                name = category,
                totalAmount = sum,
                count = list.size,
                percentage = if (totalThisWeek > 0) (sum / totalThisWeek).toFloat() else 0f
            )
        }.sortedByDescending { it.totalAmount }

        // Proyección: promedio diario de los últimos 28 días (con un mínimo de 7 días para no inflar el promedio)
        val windowStart = now - PROJECTION_WINDOW_DAYS * DAY_MS
        val recentAnt = expenses.filter { it.isAntExpense && it.dateTimestamp in windowStart..now }
        val projectedMonth = if (recentAnt.isEmpty()) {
            0.0
        } else {
            val first = recentAnt.minOf { it.dateTimestamp }
            // Días de calendario cubiertos: el primer día y hoy cuentan los dos
            val days = (Math.round((startOfDay(now) - startOfDay(first)).toDouble() / DAY_MS).toInt() + 1)
                .coerceIn(MIN_PROJECTION_DAYS, PROJECTION_WINDOW_DAYS)
            recentAnt.sumOf { it.amount } / days * DAYS_PER_MONTH
        }
        val projectedYear = projectedMonth * 12

        return AnalisisUiState(
            totalAntExpenses = totalAnt,
            previousWeekExpenses = previousAnt,
            percentageChange = percentChange,
            isIncrease = totalAnt >= previousAnt,
            categoryStats = stats,
            projectedMonthly = projectedMonth,
            projectedYearly = projectedYear,
            potentialSavings = projectedYear / 2 // Ahorrarías la mitad
        )
    }
}
