package com.hormi.hormiapp.domain.usecase.preferences

import com.hormi.hormiapp.data.preferences.UserPreferencesRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SetOnboardingCompletedUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    suspend operator fun invoke() {
        repository.setOnboardingCompleted()
    }
}

class GetOnboardingStatusUseCase @Inject constructor(
    private val repository: UserPreferencesRepository
) {
    operator fun invoke(): Flow<Boolean> {
        return repository.hasCompletedOnboarding
    }
}
