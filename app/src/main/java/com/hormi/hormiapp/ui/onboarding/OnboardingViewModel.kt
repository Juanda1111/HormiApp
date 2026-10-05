package com.hormi.hormiapp.ui.onboarding

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.domain.usecase.preferences.SetOnboardingCompletedUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class OnboardingViewModel @Inject constructor(
    private val setOnboardingCompletedUseCase: SetOnboardingCompletedUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(OnboardingUiState())
    val uiState: StateFlow<OnboardingUiState> = _uiState.asStateFlow()

    fun onIncomeChange(income: String) {
        // Filtrar para que solo permita números
        val filtered = income.filter { it.isDigit() }
        _uiState.value = _uiState.value.copy(income = filtered, error = null)
    }

    fun completeOnboarding(onSuccess: () -> Unit) {
        val currentState = _uiState.value
        if (currentState.income.isBlank() || currentState.income.toIntOrNull() == null || currentState.income.toInt() == 0) {
            _uiState.value = currentState.copy(error = "Debes ingresar un ingreso válido")
            return
        }

        viewModelScope.launch {
            setOnboardingCompletedUseCase(currentState.income)
            onSuccess()
        }
    }
}

data class OnboardingUiState(
    val income: String = "",
    val error: String? = null
)
