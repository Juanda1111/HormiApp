package com.hormi.hormiapp.ui.configuracion

import androidx.lifecycle.ViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

data class ConfiguracionUiState(
    val selectedCurrency: String = "COP $",
    val isReminderEnabled: Boolean = true,
    val selectedReminderTime: String = "20:00",
    val selectedTheme: String = "Sistema",
    val weeklyBudget: Double = 300000.0,
    val antExpenseThreshold: Double = 15000.0
)

@HiltViewModel
class ConfiguracionViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(ConfiguracionUiState())
    val uiState: StateFlow<ConfiguracionUiState> = _uiState.asStateFlow()

    fun updateCurrency(currency: String) {
        _uiState.value = _uiState.value.copy(selectedCurrency = currency)
    }

    fun toggleReminder(enabled: Boolean) {
        _uiState.value = _uiState.value.copy(isReminderEnabled = enabled)
    }

    fun updateReminderTime(time: String) {
        _uiState.value = _uiState.value.copy(selectedReminderTime = time)
    }

    fun updateTheme(theme: String) {
        _uiState.value = _uiState.value.copy(selectedTheme = theme)
    }
}
