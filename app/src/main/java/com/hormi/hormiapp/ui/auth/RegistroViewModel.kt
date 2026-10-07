package com.hormi.hormiapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.domain.usecase.preferences.ClearAllDataUseCase
import com.hormi.hormiapp.domain.usecase.preferences.GetUserNameUseCase
import com.hormi.hormiapp.domain.usecase.preferences.SaveUserDataUseCase
import com.hormi.hormiapp.domain.usecase.preferences.StartDemoUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RegistroViewModel @Inject constructor(
    private val saveUserDataUseCase: SaveUserDataUseCase,
    private val clearAllDataUseCase: ClearAllDataUseCase,
    private val startDemoUseCase: StartDemoUseCase,
    private val getUserNameUseCase: GetUserNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RegistroUiState())
    val uiState: StateFlow<RegistroUiState> = _uiState.asStateFlow()

    init {
        // Si ya hay una cuenta en el dispositivo, crear otra la reemplaza: se avisa en pantalla
        viewModelScope.launch {
            val existing = getUserNameUseCase().firstOrNull()
            _uiState.value = _uiState.value.copy(hasExistingAccount = !existing.isNullOrBlank())
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

    fun onConfirmPinChange(confirmPin: String) {
        if (confirmPin.length <= 4) {
            _uiState.value = _uiState.value.copy(confirmPin = confirmPin, error = null)
        }
    }

    fun onSecurityAnswerChange(answer: String) {
        _uiState.value = _uiState.value.copy(securityAnswer = answer, error = null)
    }

    fun togglePinVisibility() {
        _uiState.value = _uiState.value.copy(pinVisible = !_uiState.value.pinVisible)
    }

    fun toggleConfirmPinVisibility() {
        _uiState.value = _uiState.value.copy(confirmPinVisible = !_uiState.value.confirmPinVisible)
    }

    fun onRegisterClick(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.nombre.isBlank()) {
            _uiState.value = currentState.copy(error = "El nombre no puede estar vacío")
            return
        }
        if (currentState.pin.length != 4) {
            _uiState.value = currentState.copy(error = "El PIN debe tener 4 dígitos")
            return
        }
        if (currentState.pin != currentState.confirmPin) {
            _uiState.value = currentState.copy(error = "Los PINs no coinciden")
            return
        }
        if (currentState.securityAnswer.isBlank()) {
            _uiState.value = currentState.copy(error = "Debes responder la pregunta de seguridad")
            return
        }

        // Una cuenta nueva siempre empieza vacía: se borran gastos, metas y ajustes de cualquier cuenta anterior
        viewModelScope.launch {
            clearAllDataUseCase()
            saveUserDataUseCase(
                name = currentState.nombre,
                pin = currentState.pin,
                answer = currentState.securityAnswer
            )
            onSuccess()
        }
    }

    /** Crea la cuenta "Demo" con datos de ejemplo y entra directo a Inicio. */
    fun onDemoClick(onSuccess: () -> Unit) {
        viewModelScope.launch {
            startDemoUseCase()
            onSuccess()
        }
    }
}

data class RegistroUiState(
    val nombre: String = "",
    val pin: String = "",
    val confirmPin: String = "",
    val securityAnswer: String = "",
    val pinVisible: Boolean = false,
    val confirmPinVisible: Boolean = false,
    val hasExistingAccount: Boolean = false,
    val error: String? = null
)
