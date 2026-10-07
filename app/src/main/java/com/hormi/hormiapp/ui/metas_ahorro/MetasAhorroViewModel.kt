package com.hormi.hormiapp.ui.metas_ahorro

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.hormi.hormiapp.data.local.entity.GoalEntity
import com.hormi.hormiapp.data.repository.GoalRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import javax.inject.Inject

data class MetasAhorroUiState(
    val goals: List<GoalEntity> = emptyList(),
    val isLoading: Boolean = true
)

@HiltViewModel
class MetasAhorroViewModel @Inject constructor(
    private val goalRepository: GoalRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(MetasAhorroUiState())
    val uiState: StateFlow<MetasAhorroUiState> = _uiState.asStateFlow()

    init {
        loadGoals()
    }

    private fun loadGoals() {
        viewModelScope.launch {
            goalRepository.getAllGoals().collectLatest { list ->
                _uiState.value = _uiState.value.copy(
                    goals = list,
                    isLoading = false
                )
            }
        }
    }

    fun deleteGoal(goal: GoalEntity) {
        viewModelScope.launch {
            goalRepository.deleteGoal(goal)
        }
    }
}
