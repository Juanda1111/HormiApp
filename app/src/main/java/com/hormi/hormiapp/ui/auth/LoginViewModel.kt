package com.hormi.hormiapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.domain.usecase.preferences.GetUserNameUseCase
import com.hormi.hormiapp.domain.usecase.preferences.GetUserPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class LoginViewModel @Inject constructor(
    private val getUserNameUseCase: GetUserNameUseCase,
    private val getUserPinUseCase: GetUserPinUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Cargar el nombre del usuario al iniciar la pantalla
        viewModelScope.launch {
            val name = getUserNameUseCase().firstOrNull() ?: ""
            _uiState.value = _uiState.value.copy(nombre = name)
        }
    }

    fun onPinChange(pin: String) {
        if (pin.length <= 4) {
            _uiState.value = _uiState.value.copy(pin = pin, error = null)
        }
    }

    fun togglePinVisibility() {
        _uiState.value = _uiState.value.copy(pinVisible = !_uiState.value.pinVisible)
    }

    fun onLoginClick(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.pin.length != 4) {
            _uiState.value = currentState.copy(error = "El PIN debe tener 4 dígitos")
            return
        }

        viewModelScope.launch {
            val savedPin = getUserPinUseCase().firstOrNull()
            if (savedPin == currentState.pin) {
                // PIN correcto
                onSuccess()
            } else {
                // PIN incorrecto
                _uiState.value = currentState.copy(error = "PIN incorrecto")
            }
        }
    }
}

data class LoginUiState(
    val nombre: String = "",
    val pin: String = "",
    val pinVisible: Boolean = false,
    val error: String? = null
)
