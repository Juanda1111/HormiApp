package com.hormi.hormiapp.ui.perfil

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.data.preferences.UserPreferencesRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class PerfilUiState(
    val userName: String = "",
    val monthlyIncome: Double = 0.0,
    val initialLetter: String = "U"
)

@HiltViewModel
class PerfilViewModel @Inject constructor(
    private val preferencesRepository: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(PerfilUiState())
    val uiState: StateFlow<PerfilUiState> = _uiState.asStateFlow()

    init {
        loadUserProfile()
    }

    private fun loadUserProfile() {
        viewModelScope.launch {
            preferencesRepository.userName.collectLatest { name ->
                val actualName = name ?: "Usuario"
                val letter = if (actualName.isNotEmpty()) actualName.first().uppercase() else "U"
                _uiState.value = _uiState.value.copy(userName = actualName, initialLetter = letter)
            }
        }

        viewModelScope.launch {
            preferencesRepository.monthlyIncome.collectLatest { incomeString ->
                val income = incomeString?.toDoubleOrNull() ?: 0.0
                _uiState.value = _uiState.value.copy(monthlyIncome = income)
            }
        }
    }
}
