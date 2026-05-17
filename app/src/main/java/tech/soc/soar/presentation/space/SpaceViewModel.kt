package tech.soc.soar.presentation.space

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.usecase.LogoutUseCase

class SpaceViewModel(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(SpaceUiState())
    val state: StateFlow<SpaceUiState> = _state.asStateFlow()

    private val _effect = Channel<SpaceEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: SpaceEvent) {
        when (event) {
            SpaceEvent.HomeClicked -> {
                viewModelScope.launch {
                    _effect.send(SpaceEffect.NavigateToHome)
                }
            }

            SpaceEvent.LogoutClicked -> {
                logout()
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update {
                it.copy(isLoading = true)
            }

            when (logoutUseCase()) {
                is AppResult.Success,
                is AppResult.Error -> {
                    _state.update {
                        it.copy(isLoading = false)
                    }

                    _effect.send(SpaceEffect.NavigateToLogin)
                }
            }
        }
    }
}