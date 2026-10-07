package com.hormi.hormiapp.ui.ingresos

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
import java.util.Calendar

data class IngresosUiState(
    val baseIncome: Double = 0.0,
    val totalExtraIncome: Double = 0.0,
    val extraIncomesList: List<TransactionEntity> = emptyList(),
    val currentMonthName: String = ""
)

@HiltViewModel
class IngresosViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository,
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(IngresosUiState())
    val uiState: StateFlow<IngresosUiState> = _uiState.asStateFlow()

    init {
        setupCurrentMonth()
        loadData()
    }

    private fun setupCurrentMonth() {
        val calendar = Calendar.getInstance()
        val monthNames = arrayOf(
            "enero", "febrero", "marzo", "abril", "mayo", "junio",
            "julio", "agosto", "septiembre", "octubre", "noviembre", "diciembre"
        )
        val month = monthNames[calendar.get(Calendar.MONTH)]
        val year = calendar.get(Calendar.YEAR)
        _uiState.value = _uiState.value.copy(currentMonthName = "$month $year")
    }

    private fun loadData() {
        // Load base income
        viewModelScope.launch {
            preferencesRepository.monthlyIncome.collectLatest { incomeStr ->
                val income = incomeStr?.toDoubleOrNull() ?: 0.0
                _uiState.value = _uiState.value.copy(baseIncome = income)
            }
        }

        // Load extra incomes
        viewModelScope.launch {
            transactionRepository.getAllTransactions().collectLatest { allTx ->
                val monthStart = Calendar.getInstance().apply {
                    set(Calendar.DAY_OF_MONTH, 1)
                    set(Calendar.HOUR_OF_DAY, 0)
                    set(Calendar.MINUTE, 0)
                    set(Calendar.SECOND, 0)
                    set(Calendar.MILLISECOND, 0)
                }.timeInMillis
                val incomes = allTx.filter { it.type == "INCOME" && it.dateTimestamp >= monthStart }
                val extraTotal = incomes.sumOf { it.amount }
                _uiState.value = _uiState.value.copy(
                    extraIncomesList = incomes.sortedByDescending { it.dateTimestamp },
                    totalExtraIncome = extraTotal
                )
            }
        }
    }

    fun updateMonthlyIncome(newIncome: String) {
        val incomeValue = newIncome.filter { it.isDigit() }.toLongOrNull() ?: return
        viewModelScope.launch {
            preferencesRepository.saveMonthlyIncome(incomeValue.toString())
        }
    }

    fun addExtraIncome(amountText: String, description: String, dateTimestamp: Long) {
        val amount = amountText.filter { it.isDigit() }.toDoubleOrNull() ?: return
        if (amount <= 0) return
        viewModelScope.launch {
            transactionRepository.insertTransaction(
                TransactionEntity(
                    amount = amount,
                    type = "INCOME",
                    category = "Ingreso extra",
                    description = description.trim(),
                    dateTimestamp = dateTimestamp,
                    isAntExpense = false
                )
            )
        }
    }
}
