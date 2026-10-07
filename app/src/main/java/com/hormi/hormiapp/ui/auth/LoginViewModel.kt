package com.hormi.hormiapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.data.preferences.LoginResult
import com.hormi.hormiapp.data.preferences.UserPreferencesRepository
import com.hormi.hormiapp.domain.usecase.preferences.GetUserNameUseCase
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
    private val preferences: UserPreferencesRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(LoginUiState())
    val uiState: StateFlow<LoginUiState> = _uiState.asStateFlow()

    init {
        // Se sugiere la última cuenta usada, pero el nombre se puede cambiar para entrar con otra
        viewModelScope.launch {
            val name = getUserNameUseCase().firstOrNull() ?: ""
            if (_uiState.value.nombre.isEmpty()) {
                _uiState.value = _uiState.value.copy(nombre = name)
            }
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.value = _uiState.value.copy(nombre = nombre, error = null)
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

        if (currentState.nombre.isBlank()) {
            _uiState.value = currentState.copy(error = "Escribe tu nombre de usuario")
            return
        }
        if (currentState.pin.length != 4) {
            _uiState.value = currentState.copy(error = "El PIN debe tener 4 dígitos")
            return
        }

        viewModelScope.launch {
            when (preferences.login(currentState.nombre, currentState.pin)) {
                LoginResult.OK -> onSuccess()
                LoginResult.NOT_FOUND -> _uiState.value = currentState.copy(error = "No existe una cuenta con ese nombre")
                LoginResult.WRONG_PIN -> _uiState.value = currentState.copy(error = "PIN incorrecto")
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
