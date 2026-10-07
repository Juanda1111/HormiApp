package com.hormi.hormiapp.ui.inicio

import com.hormi.hormiapp.data.local.entity.TransactionEntity
import com.hormi.hormiapp.ui.analisis.AnalysisCalculator
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class DashboardCalculatorTest {

    private val day = 86_400_000L

    /** Miércoles 7 de octubre de 2026, 15:00. La semana va del lunes 5 al domingo 11. */
    private val now: Long = Calendar.getInstance().apply {
        clear()
        set(2026, Calendar.OCTOBER, 7, 15, 0, 0)
    }.timeInMillis

    private val monday = AnalysisCalculator.startOfWeek(now)

    private fun expense(amount: Double, at: Long, ant: Boolean = false, category: String = "Comida") =
        TransactionEntity(amount = amount, type = "EXPENSE", category = category, description = "", dateTimestamp = at, isAntExpense = ant)

    private fun income(amount: Double, at: Long) =
        TransactionEntity(amount = amount, type = "INCOME", category = "Ingreso extra", description = "", dateTimestamp = at, isAntExpense = false)

    private fun at(month: Int, dayOfMonth: Int): Long = Calendar.getInstance().apply {
        clear(); set(2026, month, dayOfMonth, 12, 0, 0)
    }.timeInMillis

    @Test
    fun saldoYTotales_soloDelMesActual() {
        val result = DashboardCalculator.calculate(
            baseIncome = 1_000_000.0,
            transactions = listOf(
                expense(100_000.0, at(Calendar.OCTOBER, 2)),
                expense(50_000.0, at(Calendar.OCTOBER, 6)),
                expense(900_000.0, at(Calendar.SEPTEMBER, 20)),   // mes pasado: no cuenta
                income(200_000.0, at(Calendar.OCTOBER, 3)),
                income(999_000.0, at(Calendar.SEPTEMBER, 15))      // mes pasado: no cuenta
            ),
            now = now
        )
        assertEquals(150_000.0, result.totalExpenses, 0.001)
        assertEquals(1_200_000.0, result.totalIncome, 0.001)
        assertEquals(1_050_000.0, result.availableBalance, 0.001)
    }

    @Test
    fun presupuestoSemanal_esElVeinticincoPorCientoDelIngresoBase() {
        val result = DashboardCalculator.calculate(2_000_000.0, listOf(income(500_000.0, now)), now)
        // El ingreso extra no cambia el presupuesto semanal (igual que en Configuración)
        assertEquals(500_000.0, result.weeklyBudget, 0.001)
    }

    @Test
    fun gastoSemanal_soloCuentaDeLunesADomingoDeEstaSemana() {
        val result = DashboardCalculator.calculate(
            1_000_000.0,
            listOf(
                expense(20_000.0, monday + 1_000L),
                expense(10_000.0, now),
                expense(300_000.0, monday - 1_000L),   // domingo pasado
                income(80_000.0, now)                    // un ingreso no es gasto
            ),
            now
        )
        assertEquals(30_000.0, result.weeklySpent, 0.001)
        assertEquals(220_000.0, result.weeklyRemaining, 0.001)
        assertEquals(0.12f, result.weeklyPercent, 0.001f)
    }

    @Test
    fun presupuestoExcedido_noPasaDelCienPorCiento() {
        val result = DashboardCalculator.calculate(100_000.0, listOf(expense(80_000.0, now)), now)
        assertEquals(1f, result.weeklyPercent, 0.001f)
        assertEquals(0.0, result.weeklyRemaining, 0.001)
    }

    @Test
    fun mayorGastoHormiga_esDelMes() {
        val result = DashboardCalculator.calculate(
            1_000_000.0,
            listOf(
                expense(2_500.0, now, ant = true, category = "Café y snacks"),
                expense(4_000.0, now - day, ant = true, category = "Café y snacks"),
                expense(3_200.0, now, ant = true, category = "Transporte"),
                expense(800_000.0, at(Calendar.SEPTEMBER, 10), ant = true, category = "Domicilios"), // mes pasado
                expense(45_000.0, now, ant = false, category = "Salidas")                             // no es hormiga
            ),
            now
        )
        assertEquals("Café y snacks", result.highestAntExpenseCategory)
        assertEquals(2, result.highestAntExpenseCount)
        assertEquals(6_500.0, result.highestAntExpenseTotal, 0.001)
    }

    @Test
    fun ultimosGastos_sonSoloGastosDeLosMasRecientesAMasAntiguos() {
        val txs = (0 until 8).map { expense(1_000.0 + it, now - it * day) } + income(5_000.0, now + 1)
        val result = DashboardCalculator.calculate(1_000_000.0, txs, now)
        assertEquals(5, result.recentTransactions.size)
        assertTrue(result.recentTransactions.all { it.type == "EXPENSE" })
        assertEquals(listOf(1000.0, 1001.0, 1002.0, 1003.0, 1004.0), result.recentTransactions.map { it.amount })
    }

    @Test
    fun sinDatos_ceroGastosSaldoIgualAlIngreso() {
        val result = DashboardCalculator.calculate(1_500_000.0, emptyList(), now)
        assertEquals(1_500_000.0, result.availableBalance, 0.001)
        assertEquals(0.0, result.weeklySpent, 0.001)
        assertEquals("Ninguno", result.highestAntExpenseCategory)
        assertTrue(result.recentTransactions.isEmpty())
    }
}
