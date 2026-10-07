package com.hormi.hormiapp.ui.configuracion

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.domain.usecase.preferences.ClearAllDataUseCase
import com.hormi.hormiapp.data.preferences.UserPreferencesRepository
import com.hormi.hormiapp.reminder.ReminderScheduler
import dagger.hilt.android.lifecycle.HiltViewModel
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import javax.inject.Inject

data class ConfiguracionUiState(
    val selectedCurrency: String = "COP $",
    val isReminderEnabled: Boolean = false,
    val selectedReminderTime: String = "20:00",
    val selectedTheme: String = "Sistema",
    val monthlyIncome: Double = 0.0,
    val antExpenseThreshold: Double = 15000.0
) {
    /** El presupuesto semanal es el 25% del ingreso mensual (igual que en Inicio). */
    val weeklyBudget: Double get() = monthlyIncome * 0.25
}

@HiltViewModel
class ConfiguracionViewModel @Inject constructor(
    @ApplicationContext private val context: Context,
    private val userPreferencesRepository: UserPreferencesRepository,
    private val clearAllDataUseCase: ClearAllDataUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(ConfiguracionUiState())
    val uiState: StateFlow<ConfiguracionUiState> = _uiState.asStateFlow()

    init {
        viewModelScope.launch {
            userPreferencesRepository.antExpenseThreshold.collect { threshold ->
                _uiState.value = _uiState.value.copy(antExpenseThreshold = threshold)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.monthlyIncome.collect { income ->
                _uiState.value = _uiState.value.copy(monthlyIncome = income?.toDoubleOrNull() ?: 0.0)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.currency.collect { currency ->
                _uiState.value = _uiState.value.copy(selectedCurrency = currency)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.theme.collect { theme ->
                _uiState.value = _uiState.value.copy(selectedTheme = theme)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.reminderEnabled.collect { enabled ->
                _uiState.value = _uiState.value.copy(isReminderEnabled = enabled)
            }
        }
        viewModelScope.launch {
            userPreferencesRepository.reminderTime.collect { time ->
                _uiState.value = _uiState.value.copy(selectedReminderTime = time)
            }
        }
    }

    fun updateCurrency(currency: String) {
        viewModelScope.launch { userPreferencesRepository.updateCurrency(currency) }
    }

    fun toggleReminder(enabled: Boolean) {
        val time = _uiState.value.selectedReminderTime
        viewModelScope.launch { userPreferencesRepository.updateReminder(enabled, time) }
        if (enabled) ReminderScheduler.schedule(context, time) else ReminderScheduler.cancel(context)
    }

    fun updateReminderTime(time: String) {
        val enabled = _uiState.value.isReminderEnabled
        viewModelScope.launch { userPreferencesRepository.updateReminder(enabled, time) }
        if (enabled) ReminderScheduler.schedule(context, time)
    }

    fun updateTheme(theme: String) {
        viewModelScope.launch { userPreferencesRepository.updateTheme(theme) }
    }

    fun updateMonthlyIncome(text: String) {
        val value = text.filter { it.isDigit() }.toLongOrNull() ?: return
        if (value <= 0) return
        viewModelScope.launch { userPreferencesRepository.saveMonthlyIncome(value.toString()) }
    }

    fun updateAntThreshold(text: String) {
        val value = text.filter { it.isDigit() }.toDoubleOrNull() ?: return
        if (value <= 0) return
        viewModelScope.launch { userPreferencesRepository.updateAntExpenseThreshold(value) }
    }

    /** Borra transacciones, metas y todas las preferencias (incluida la cuenta local). */
    fun deleteAllData(onDone: () -> Unit) {
        viewModelScope.launch {
            clearAllDataUseCase()
            onDone()
        }
    }
}
