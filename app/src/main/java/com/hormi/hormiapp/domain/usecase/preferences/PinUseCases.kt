package com.hormi.hormiapp.domain.usecase.preferences

import com.hormi.hormiapp.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SaveUserDataUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke(name: String, pin: String, answer: String) {
        repository.saveUserData(name, pin, answer)
    }
}

class GetUserPinUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<String?> {
        return repository.userPin
    }
}
