package com.hormi.hormiapp.ui.analisis

import com.hormi.hormiapp.data.local.entity.TransactionEntity
import java.util.Calendar
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Test

class AnalysisCalculatorTest {

    private val day = 86_400_000L

    /** Miércoles 7 de octubre de 2026, 15:00 (hora local). La semana va del lunes 5 al domingo 11. */
    private val now: Long = Calendar.getInstance().apply {
        clear()
        set(2026, Calendar.OCTOBER, 7, 15, 0, 0)
    }.timeInMillis

    private fun monday(): Long = AnalysisCalculator.startOfWeek(now)

    private fun expense(amount: Double, at: Long, ant: Boolean = true, category: String = "Café y snacks") =
        TransactionEntity(
            amount = amount, type = "EXPENSE", category = category,
            description = "", dateTimestamp = at, isAntExpense = ant
        )

    @Test
    fun startOfWeek_esElLunesALasCeroHoras() {
        val cal = Calendar.getInstance().apply { timeInMillis = monday() }
        assertEquals(Calendar.MONDAY, cal.get(Calendar.DAY_OF_WEEK))
        assertEquals(5, cal.get(Calendar.DAY_OF_MONTH))
        assertEquals(0, cal.get(Calendar.HOUR_OF_DAY))
    }

    @Test
    fun startOfWeek_domingoPerteneceALaSemanaQueTermina() {
        val sunday = Calendar.getInstance().apply { clear(); set(2026, Calendar.OCTOBER, 11, 20, 0, 0) }.timeInMillis
        assertEquals(monday(), AnalysisCalculator.startOfWeek(sunday))
    }

    @Test
    fun totalSemana_soloCuentaGastosHormigaDeEstaSemana() {
        val result = AnalysisCalculator.calculate(
            listOf(
                expense(2500.0, now - 1 * 3_600_000L),        // hoy
                expense(4000.0, monday() + 1_000L),            // lunes
                expense(9999.0, monday() - 1_000L),            // domingo pasado: semana anterior
                expense(45000.0, now, ant = false)             // gasto grande: no es hormiga
            ),
            now
        )
        assertEquals(6500.0, result.totalAntExpenses, 0.001)
    }

    @Test
    fun semanaAnterior_usaGastosReales() {
        val result = AnalysisCalculator.calculate(
            listOf(
                expense(10000.0, now),
                expense(4000.0, monday() - 1 * day),
                expense(6000.0, monday() - 5 * day)
            ),
            now
        )
        assertEquals(10000.0, result.previousWeekExpenses, 0.001)
        assertEquals(0.0f, result.percentageChange, 0.001f)
        assertTrue(result.isIncrease) // 10.000 vs 10.000 no es una baja
    }

    @Test
    fun comparacion_aumento() {
        val result = AnalysisCalculator.calculate(
            listOf(expense(15000.0, now), expense(10000.0, monday() - 2 * day)), now
        )
        assertEquals(0.5f, result.percentageChange, 0.001f)
        assertTrue(result.isIncrease)
    }

    @Test
    fun comparacion_disminucion() {
        val result = AnalysisCalculator.calculate(
            listOf(expense(5000.0, now), expense(10000.0, monday() - 2 * day)), now
        )
        assertEquals(-0.5f, result.percentageChange, 0.001f)
        assertFalse(result.isIncrease)
    }

    @Test
    fun sinSemanaAnterior_noHayComparacion() {
        val result = AnalysisCalculator.calculate(listOf(expense(5000.0, now)), now)
        assertEquals(0.0, result.previousWeekExpenses, 0.001)
        assertEquals(0f, result.percentageChange, 0.001f)
    }

    @Test
    fun categorias_soloDeEstaSemanaIncluyendoGastosGrandes() {
        val result = AnalysisCalculator.calculate(
            listOf(
                expense(3000.0, now, category = "Café y snacks"),
                expense(1000.0, now - 1 * 3_600_000L, category = "Café y snacks"),
                expense(36000.0, now, ant = false, category = "Salidas"),
                expense(80000.0, monday() - day, ant = false, category = "Universidad") // semana pasada
            ),
            now
        )
        assertEquals(listOf("Salidas", "Café y snacks"), result.categoryStats.map { it.name })
        assertEquals(2, result.categoryStats[1].count)
        assertEquals(0.9f, result.categoryStats[0].percentage, 0.001f)
    }

    @Test
    fun proyeccion_usaPromedioDiarioNoElTotalHistorico() {
        // 28 días de datos, 2.000 por día: 56.000 en total -> 2.000/día -> 60.000 al mes
        val data = (0 until 28).map { expense(2000.0, now - it * day) }
        val result = AnalysisCalculator.calculate(data, now)
        assertEquals(60000.0, result.projectedMonthly, 1000.0)
        assertEquals(result.projectedMonthly * 12, result.projectedYearly, 0.001)
        assertEquals(result.projectedYearly / 2, result.potentialSavings, 0.001)
    }

    @Test
    fun proyeccion_ignoraGastosDeMasDe28Dias() {
        val data = listOf(expense(500000.0, now - 60 * day), expense(7000.0, now))
        val result = AnalysisCalculator.calculate(data, now)
        // Solo cuenta el de hoy: 7.000 en una ventana mínima de 7 días -> 1.000/día -> 30.000 al mes
        assertEquals(30000.0, result.projectedMonthly, 0.001)
    }

    @Test
    fun sinDatos_todoEnCero() {
        val result = AnalysisCalculator.calculate(emptyList(), now)
        assertEquals(0.0, result.totalAntExpenses, 0.001)
        assertEquals(0.0, result.projectedMonthly, 0.001)
        assertTrue(result.categoryStats.isEmpty())
    }
}
