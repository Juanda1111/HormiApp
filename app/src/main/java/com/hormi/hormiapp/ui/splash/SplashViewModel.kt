package com.hormi.hormiapp.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.domain.usecase.preferences.GetUserPinUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor(
    private val getUserPinUseCase: GetUserPinUseCase
) : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        viewModelScope.launch {
            val pin = getUserPinUseCase().firstOrNull()
            if (pin != null) {
                // Si ya existe un PIN guardado, el usuario ya se registró
                _destination.value = SplashDestination.Login
            } else {
                // Si no hay PIN, es un usuario nuevo
                _destination.value = SplashDestination.Registro
            }
        }
    }
}

enum class SplashDestination {
    Login,
    Registro
}
