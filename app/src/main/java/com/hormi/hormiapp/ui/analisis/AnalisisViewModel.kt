package com.hormi.hormiapp.ui.analisis

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class CategoryStat(
    val name: String,
    val totalAmount: Double,
    val count: Int,
    val percentage: Float
)

data class AnalisisUiState(
    val totalAntExpenses: Double = 0.0,
    val previousWeekExpenses: Double = 0.0,
    val percentageChange: Float = 0f,
    val isIncrease: Boolean = true,
    
    val categoryStats: List<CategoryStat> = emptyList(),
    
    val projectedMonthly: Double = 0.0,
    val projectedYearly: Double = 0.0,
    val potentialSavings: Double = 0.0
)

@HiltViewModel
class AnalisisViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AnalisisUiState())
    val uiState: StateFlow<AnalisisUiState> = _uiState.asStateFlow()

    init {
        loadAnalysisData()
    }

    private fun loadAnalysisData() {
        viewModelScope.launch {
            transactionRepository.getAllTransactions().collectLatest { transactions ->
                _uiState.value = AnalysisCalculator.calculate(transactions)
            }
        }
    }
}
