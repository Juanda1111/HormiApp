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

    init {
        loadGastos()
    }

    private fun loadGastos() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions().collectLatest { allTransactions ->
                val expenses = allTransactions.filter { it.type == "EXPENSE" }
                
                // Agrupar por fecha
                val grouped = expenses.groupBy { getDayLabel(it.dateTimestamp) }
                
                // Calcular totales
                val totalExpenses = expenses.sumOf { it.amount }
                val totalAntExpenses = expenses.filter { it.isAntExpense }.sumOf { it.amount }
                
                _uiState.value = _uiState.value.copy(
                    totalExpenses = totalExpenses,
                    totalAntExpenses = totalAntExpenses,
                    groupedExpenses = grouped
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

data class GastosUiState(
    val totalExpenses: Double = 0.0,
    val totalAntExpenses: Double = 0.0,
    val groupedExpenses: Map<String, List<TransactionEntity>> = emptyMap()
)
