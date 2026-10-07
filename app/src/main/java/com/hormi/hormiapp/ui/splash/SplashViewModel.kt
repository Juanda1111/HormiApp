package com.hormi.hormiapp.ui.splash

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SplashViewModel @Inject constructor() : ViewModel() {

    private val _destination = MutableStateFlow<SplashDestination?>(null)
    val destination: StateFlow<SplashDestination?> = _destination.asStateFlow()

    init {
        checkUserStatus()
    }

    private fun checkUserStatus() {
        viewModelScope.launch {
            // La app siempre arranca en Iniciar sesión; desde ahí se puede crear una cuenta
            _destination.value = SplashDestination.Login
        }
    }
}

enum class SplashDestination {
    Login,
    Registro
}
