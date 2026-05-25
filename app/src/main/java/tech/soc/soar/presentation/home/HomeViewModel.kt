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
import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.model.UserRoles
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase
import tech.soc.soar.shared.domain.auth.usecase.LogoutUseCase

class HomeViewModel(
    private val logoutUseCase: LogoutUseCase,
    private val checkSessionUseCase: CheckSessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(HomeUiState())
    val state: StateFlow<HomeUiState> = _state.asStateFlow()

    private val _effect = Channel<HomeEffect>()
    val effect = _effect.receiveAsFlow()

    init {
        loadSpaces()
    }

    fun onEvent(event: HomeEvent) {
        when (event) {
            HomeEvent.HomeClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToHome)
                }
            }

            HomeEvent.LogoutClicked -> {
                logout()
            }

            HomeEvent.ChangePasswordClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToChangePassword)
                }
            }

            is HomeEvent.SpaceClicked -> {
                viewModelScope.launch {
                    _effect.send(HomeEffect.NavigateToSpace(event.spaceName))
                }
            }
        }
    }

    private fun loadSpaces() {
        viewModelScope.launch {
            val sessionState = checkSessionUseCase()

            val session = when (sessionState) {
                is SessionState.Authenticated -> sessionState.session
                is SessionState.RequiresTwoFactor -> sessionState.session
                SessionState.Loading,
                SessionState.Unauthenticated -> null
            }

            val roles = session?.roles ?: emptyList()
            val spaces = UserRoles.getSpaces(roles)

            val accountUid = session?.user?.uid?.toString()

            _state.update {
                it.copy(
                    spaces = spaces,
                    currentAccountUid = accountUid
                )
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

            when (logoutUseCase()) {
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