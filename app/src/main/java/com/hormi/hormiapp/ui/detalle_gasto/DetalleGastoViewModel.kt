package com.hormi.hormiapp.ui.detalle_gasto

import androidx.lifecycle.SavedStateHandle
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

data class DetalleGastoUiState(
    val transaction: TransactionEntity? = null,
    val isLoading: Boolean = true,
    val isDeleted: Boolean = false
)

@HiltViewModel
class DetalleGastoViewModel @Inject constructor(
    private val transactionRepository: TransactionRepository,
    savedStateHandle: SavedStateHandle
) : ViewModel() {

    private val _uiState = MutableStateFlow(DetalleGastoUiState())
    val uiState: StateFlow<DetalleGastoUiState> = _uiState.asStateFlow()

    private val gastoId: Int = checkNotNull(savedStateHandle["gastoId"])

    init {
        loadTransaction()
    }

    private fun loadTransaction() {
        viewModelScope.launch {
            // Suponiendo que el repositorio tiene un mtodo para obtener transaccin por ID
            // Si no lo tiene, podramos agregar getTransactionById en el DAO y Repository.
            // Por ahora filtramos desde getAllTransactions o deberamos agregar la query al DAO.
            transactionRepository.getAllTransactions().collect { list ->
                val trans = list.find { it.id == gastoId }
                _uiState.value = _uiState.value.copy(
                    transaction = trans,
                    isLoading = false
                )
            }
        }
    }

    fun deleteTransaction() {
        val trans = _uiState.value.transaction ?: return
        viewModelScope.launch {
            transactionRepository.deleteTransaction(trans)
            _uiState.value = _uiState.value.copy(isDeleted = true)
        }
    }
}
