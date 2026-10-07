package com.hormi.hormiapp.ui.add_expense

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.data.local.entity.TransactionEntity
import com.hormi.hormiapp.data.repository.TransactionRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
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
    val isSaved: Boolean = false,
    val isEditing: Boolean = false
)

@HiltViewModel
class AddExpenseViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    private val userPreferencesRepository: com.hormi.hormiapp.data.preferences.UserPreferencesRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    // Solo existe cuando se llega desde "Editar gasto"
    private val editingId: Int? = savedStateHandle.get<Int>("gastoId")
    private var editingTransaction: TransactionEntity? = null

    private val _uiState = MutableStateFlow(AddExpenseUiState())
    val uiState: StateFlow<AddExpenseUiState> = _uiState.asStateFlow()

    private var currentThreshold: Double = 15000.0

    init {
        if (editingId != null) {
            viewModelScope.launch {
                val trans = transactionRepository.getAllTransactions().first().find { it.id == editingId }
                if (trans != null) {
                    editingTransaction = trans
                    _uiState.value = _uiState.value.copy(
                        amount = trans.amount.toLong().toString(),
                        category = trans.category,
                        note = trans.description,
                        isImpulsive = trans.isAntExpense,
                        dateTimestamp = trans.dateTimestamp,
                        isEditing = true
                    )
                }
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.antExpenseThreshold.collect { threshold ->
                currentThreshold = threshold
            }
        }
    }

    fun updateAmount(newAmount: String) {
        // Filtrar solo números
        val filtered = newAmount.filter { it.isDigit() }
        _uiState.value = _uiState.value.copy(
            amount = filtered
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
            // Regla: si es menor o igual al umbral del usuario, es hormiga. Si no, depende del switch.
            val isActuallyAntExpense = if (amountValue <= currentThreshold) true else state.isImpulsive

            val transaction = TransactionEntity(
                id = editingTransaction?.id ?: 0,
                amount = amountValue,
                type = "EXPENSE",
                category = state.category,
                description = state.note,
                dateTimestamp = state.dateTimestamp,
                isAntExpense = isActuallyAntExpense
            )
            
            if (editingTransaction != null) {
                transactionRepository.updateTransaction(transaction)
            } else {
                transactionRepository.insertTransaction(transaction)
            }
            _uiState.value = _uiState.value.copy(isSaved = true)
        }
    }
}
