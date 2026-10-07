package com.hormi.hormiapp.domain.usecase.preferences

import com.hormi.hormiapp.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

/** Crea una cuenta nueva (y la deja activa). */
class SaveUserDataUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(name: String, pin: String, answer: String) {
        repository.createAccount(name, pin, answer)
    }
}

/** Nombre de la cuenta activa (se usa para rellenar el inicio de sesión). */
class GetUserNameUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<String?> {
        return repository.userName
    }
}

enum class ResetPinResult { OK, NOT_FOUND, WRONG_ANSWER }

/** Cambia el PIN de una cuenta si la respuesta de seguridad es correcta. */
class ResetPinUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(name: String, answer: String, newPin: String): ResetPinResult {
        val saved = repository.getSecurityAnswer(name) ?: return ResetPinResult.NOT_FOUND
        // Se ignoran mayúsculas, minúsculas y espacios extra
        if (!saved.trim().equals(answer.trim(), ignoreCase = true)) return ResetPinResult.WRONG_ANSWER
        repository.resetPin(name, newPin)
        return ResetPinResult.OK
    }
}
