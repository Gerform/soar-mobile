package tech.soc.soar.presentation.home

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

class HomeViewModel(
    private val logoutUseCase: LogoutUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.HomeClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToHome)
                }
            }

            HomeEvent.LogoutClicked -> logout()

            HomeEvent.ChangePasswordClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToChangePassword)
                }
            }
        }
    }

    private fun logout() {
        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    error = null
                )
            }

            when (val result = logoutUseCase()) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(isLoading = false)
                    }

                    _effect.send(HomeEffect.NavigateToLogin)
                }

                is AppResult.Error -> {
                    _state.update {
                        it.copy(isLoading = false)
                    }

                    _effect.send(HomeEffect.NavigateToLogin)
                }
            }
        }
    }
}