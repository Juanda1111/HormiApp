package com.hormi.hormiapp.ui.gastos

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.data.local.entity.TransactionEntity
import com.hormi.hormiapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale
import javax.inject.Inject

@HiltViewModel
class GastosViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(GastosUiState())
    val uiState: StateFlow<GastosUiState> = _uiState.asStateFlow()

    private val filter = MutableStateFlow(GastosFilter.SEMANA)

    init {
        loadGastos()
    }

    fun setFilter(newFilter: GastosFilter) {
        filter.value = newFilter
    }

    private fun startOf(filter: GastosFilter): Long {
        val cal = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        when (filter) {
            GastosFilter.SEMANA -> {
                // La semana empieza el lunes
                val daysSinceMonday = (cal.get(Calendar.DAY_OF_WEEK) + 5) % 7
                cal.add(Calendar.DAY_OF_YEAR, -daysSinceMonday)
            }
            GastosFilter.MES -> cal.set(Calendar.DAY_OF_MONTH, 1)
        }
        return cal.timeInMillis
    }

    private fun loadGastos() {
        viewModelScope.launch {
            combine(transactionRepository.getAllTransactions(), filter) { all, f -> all to f }
                .collectLatest { (allTransactions, currentFilter) ->
                val start = startOf(currentFilter)
                val expenses = allTransactions.filter { it.type == "EXPENSE" && it.dateTimestamp >= start }
                
                // Agrupar por fecha
                val grouped = expenses.groupBy { getDayLabel(it.dateTimestamp) }
                
                // Calcular totales
                val totalExpenses = expenses.sumOf { it.amount }
                val totalAntExpenses = expenses.filter { it.isAntExpense }.sumOf { it.amount }
                
                _uiState.value = _uiState.value.copy(
                    totalExpenses = totalExpenses,
                    totalAntExpenses = totalAntExpenses,
                    groupedExpenses = grouped,
                    filter = currentFilter
                )
            }
        }
    }

    private fun getDayLabel(timestamp: Long): String {
        val today = Calendar.getInstance()
        val date = Calendar.getInstance().apply { timeInMillis = timestamp }
        
        return when {
            today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) == date.get(Calendar.DAY_OF_YEAR) -> "Hoy"
            
            today.get(Calendar.YEAR) == date.get(Calendar.YEAR) &&
            today.get(Calendar.DAY_OF_YEAR) - 1 == date.get(Calendar.DAY_OF_YEAR) -> "Ayer"
            
            else -> {
                val format = SimpleDateFormat("dd 'de' MMMM", Locale("es", "ES"))
                format.format(Date(timestamp))
            }
        }
    }
}

enum class GastosFilter { SEMANA, MES }

data class GastosUiState(
    val filter: GastosFilter = GastosFilter.SEMANA,
    val totalExpenses: Double = 0.0,
    val totalAntExpenses: Double = 0.0,
    val groupedExpenses: Map<String, List<TransactionEntity>> = emptyMap()
)
