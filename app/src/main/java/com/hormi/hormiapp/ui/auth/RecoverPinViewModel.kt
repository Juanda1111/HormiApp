package com.hormi.hormiapp.ui.auth

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.domain.usecase.preferences.GetUserNameUseCase
import com.hormi.hormiapp.domain.usecase.preferences.ResetPinResult
import com.hormi.hormiapp.domain.usecase.preferences.ResetPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class RecoverPinViewModel @Inject constructor(
    private val resetPinUseCase: ResetPinUseCase,
    private val getUserNameUseCase: GetUserNameUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(RecoverPinUiState())
    val uiState: StateFlow<RecoverPinUiState> = _uiState.asStateFlow()

    init {
        // Se sugiere la última cuenta usada, pero se puede cambiar para recuperar otra
        viewModelScope.launch {
            val name = getUserNameUseCase().firstOrNull() ?: ""
            if (_uiState.value.nombre.isEmpty()) _uiState.value = _uiState.value.copy(nombre = name)
        }
    }

    fun onNombreChange(nombre: String) {
        _uiState.value = _uiState.value.copy(nombre = nombre, error = null)
    }

    fun onSecurityAnswerChange(answer: String) {
        _uiState.value = _uiState.value.copy(securityAnswer = answer, error = null)
    }

    fun onNewPinChange(pin: String) {
        if (pin.length <= 4) {
            _uiState.value = _uiState.value.copy(newPin = pin, error = null)
        }
    }

    fun onConfirmNewPinChange(pin: String) {
        if (pin.length <= 4) {
            _uiState.value = _uiState.value.copy(confirmNewPin = pin, error = null)
        }
    }

    fun toggleNewPinVisibility() {
        _uiState.value = _uiState.value.copy(newPinVisible = !_uiState.value.newPinVisible)
    }

    fun toggleConfirmNewPinVisibility() {
        _uiState.value = _uiState.value.copy(confirmNewPinVisible = !_uiState.value.confirmNewPinVisible)
    }

    fun onSaveNewPinClick(onSuccess: () -> Unit) {
        val currentState = _uiState.value

        if (currentState.nombre.isBlank()) {
            _uiState.value = currentState.copy(error = "Escribe tu nombre de usuario")
            return
        }
        if (currentState.securityAnswer.isBlank()) {
            _uiState.value = currentState.copy(error = "Debes ingresar tu respuesta de seguridad")
            return
        }
        if (currentState.newPin.length != 4) {
            _uiState.value = currentState.copy(error = "El PIN debe tener 4 dígitos")
            return
        }
        if (currentState.newPin != currentState.confirmNewPin) {
            _uiState.value = currentState.copy(error = "Los nuevos PINs no coinciden")
            return
        }

        viewModelScope.launch {
            when (resetPinUseCase(currentState.nombre, currentState.securityAnswer, currentState.newPin)) {
                ResetPinResult.OK -> onSuccess()
                ResetPinResult.NOT_FOUND -> _uiState.value = currentState.copy(error = "No existe una cuenta con ese nombre")
                ResetPinResult.WRONG_ANSWER -> _uiState.value = currentState.copy(error = "La respuesta de seguridad es incorrecta")
            }
        }
    }
}

data class RecoverPinUiState(
    val nombre: String = "",
    val securityAnswer: String = "",
    val newPin: String = "",
    val confirmNewPin: String = "",
    val newPinVisible: Boolean = false,
    val confirmNewPinVisible: Boolean = false,
    val error: String? = null
)
