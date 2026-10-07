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
                    // Se conserva el nombre de usuario que ya cargó loadUserName()
                    _uiState.value = DashboardCalculator.calculate(baseIncome, transactions)
                        .copy(userName = _uiState.value.userName)
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
