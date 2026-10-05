package com.hormi.hormiapp.ui.inicio

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.data.local.entity.TransactionEntity
import com.hormi.hormiapp.data.preferences.UserPreferencesRepository
import com.hormi.hormiapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class InicioViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(InicioUiState())
    val uiState: StateFlow<InicioUiState> = _uiState.asStateFlow()

    init {
        loadUserName()
        loadDashboardData()
    }

    private fun loadUserName() {
        viewModelScope.launch {
            preferencesRepository.userName.collectLatest { name ->
                _uiState.value = _uiState.value.copy(userName = name ?: "Usuario")
            }
        }
    }

    private fun loadDashboardData() {
        viewModelScope.launch {
            // Observamos tanto los ingresos como las transacciones al mismo tiempo
            preferencesRepository.monthlyIncome.collectLatest { incomeString ->
                val baseIncome = incomeString?.toDoubleOrNull() ?: 0.0

                transactionRepository.getAllTransactions().collectLatest { transactions ->
                    val totalExpenses = transactions
                        .filter { it.type == "EXPENSE" }
                        .sumOf { it.amount }

                    val extraIncome = transactions
                        .filter { it.type == "INCOME" }
                        .sumOf { it.amount }
                        
                    val totalIncome = baseIncome + extraIncome
                    val availableBalance = totalIncome - totalExpenses
                    
                    // Cálculo de Gastos Hormiga
                    val antExpenses = transactions.filter { it.isAntExpense }
                    val totalAntExpensesAmount = antExpenses.sumOf { it.amount }
                    
                    // Encontrar la mayor categoría de gasto hormiga
                    val groupedAntExpenses = antExpenses.groupBy { it.category }
                    var highestCategory = "Ninguno"
                    var highestCategoryCount = 0
                    var highestCategoryTotal = 0.0
                    
                    if (groupedAntExpenses.isNotEmpty()) {
                        val maxEntry = groupedAntExpenses.maxByOrNull { entry -> entry.value.sumOf { it.amount } }
                        if (maxEntry != null) {
                            highestCategory = maxEntry.key
                            highestCategoryCount = maxEntry.value.size
                            highestCategoryTotal = maxEntry.value.sumOf { it.amount }
                        }
                    }
                    
                    // Presupuesto semanal (mock: asume que es el 25% del ingreso total, y se gasta según los expenses de la última semana)
                    val weeklyBudget = totalIncome * 0.25
                    // Para simplificar, tomamos todos los gastos como de esta semana en el demo
                    val weeklySpent = totalExpenses
                    val weeklyRemaining = (weeklyBudget - weeklySpent).coerceAtLeast(0.0)
                    val weeklyPercent = if (weeklyBudget > 0) (weeklySpent / weeklyBudget).coerceAtMost(1.0) else 0.0

                    _uiState.value = _uiState.value.copy(
                        availableBalance = availableBalance,
                        totalIncome = totalIncome,
                        totalExpenses = totalExpenses,
                        
                        weeklyBudget = weeklyBudget,
                        weeklySpent = weeklySpent,
                        weeklyRemaining = weeklyRemaining,
                        weeklyPercent = weeklyPercent.toFloat(),
                        
                        highestAntExpenseCategory = highestCategory,
                        highestAntExpenseCount = highestCategoryCount,
                        highestAntExpenseTotal = highestCategoryTotal,
                        
                        recentTransactions = transactions.take(5) // solo los últimos 5
                    )
                }
            }
        }
    }
}

data class InicioUiState(
    val userName: String = "",
    val availableBalance: Double = 0.0,
    val totalIncome: Double = 0.0,
    val totalExpenses: Double = 0.0,
    
    val weeklyBudget: Double = 0.0,
    val weeklySpent: Double = 0.0,
    val weeklyRemaining: Double = 0.0,
    val weeklyPercent: Float = 0f,
    
    val highestAntExpenseCategory: String = "Ninguno",
    val highestAntExpenseCount: Int = 0,
    val highestAntExpenseTotal: Double = 0.0,
    
    val recentTransactions: List<TransactionEntity> = emptyList()
)
