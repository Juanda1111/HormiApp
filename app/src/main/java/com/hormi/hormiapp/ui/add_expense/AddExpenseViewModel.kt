package com.hormi.hormiapp.ui.add_expense

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.data.local.entity.TransactionEntity
import com.hormi.hormiapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

data class QuickExpense(
    val name: String,
    val amount: Double,
    val category: String,
    val isImpulsive: Boolean
)

data class AddExpenseUiState(
    val amount: String = "",
    val category: String = "Otros",
    val note: String = "",
    val isImpulsive: Boolean = false,
    val dateTimestamp: Long = System.currentTimeMillis(),
    val isSaved: Boolean = false
)

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    fun updateAmount(newAmount: String) {
        // Filtrar solo números
        val filtered = newAmount.filter { it.isDigit() }
        val amountValue = filtered.toDoubleOrNull() ?: 0.0
        
        // Auto-clasificar como gasto hormiga si es pequeño (ej. <= 15000)
        val isAutoHormiga = amountValue > 0 && amountValue <= 15000
        
        _uiState.value = _uiState.value.copy(
            amount = filtered,
            isImpulsive = if (isAutoHormiga) true else _uiState.value.isImpulsive
        )
    }

    fun updateCategory(newCategory: String) {
        _uiState.value = _uiState.value.copy(category = newCategory)
    }

    fun updateDate(newTimestamp: Long) {
        _uiState.value = _uiState.value.copy(dateTimestamp = newTimestamp)
    }

    fun updateNote(newNote: String) {
        _uiState.value = _uiState.value.copy(note = newNote)
    }

    fun toggleImpulsive(isImpulsive: Boolean) {
        _uiState.value = _uiState.value.copy(isImpulsive = isImpulsive)
    }

    fun applyQuickExpense(expense: QuickExpense) {
        _uiState.value = _uiState.value.copy(
            amount = expense.amount.toLong().toString(),
            category = expense.category,
            note = expense.name,
            isImpulsive = expense.isImpulsive
        )
    }

    fun saveExpense() {
        val state = _uiState.value
        val amountValue = state.amount.toDoubleOrNull() ?: 0.0
        
        if (amountValue <= 0) return

        viewModelScope.launch {
            val transaction = TransactionEntity(
                amount = amountValue,
                type = "EXPENSE",
                category = state.category,
                description = state.note,
                dateTimestamp = state.dateTimestamp,
                isAntExpense = state.isImpulsive
            )
            
            transactionRepository.insertTransaction(transaction)
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
