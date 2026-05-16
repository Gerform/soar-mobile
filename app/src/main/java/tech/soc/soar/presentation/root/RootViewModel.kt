package tech.soc.soar.presentation.root

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase

class RootViewModel(
    private val checkSessionUseCase: CheckSessionUseCase
) : ViewModel() {

    private val _state = MutableStateFlow<RootUiState>(RootUiState.Loading)
    val state: StateFlow<RootUiState> = _state.asStateFlow()

    init {
        checkSession()
    }

    fun checkSession() {
        viewModelScope.launch {
            _state.value = RootUiState.Loading

            _state.value = when (checkSessionUseCase()) {
                SessionState.Loading -> RootUiState.Loading
                SessionState.Unauthenticated -> RootUiState.Unauthenticated
                is SessionState.RequiresTwoFactor -> RootUiState.RequiresTwoFactor
                is SessionState.Authenticated -> RootUiState.Authenticated
            }
        }
    }

    fun onLoggedIn() {
        checkSession()
    }

    fun onTwoFactorConfirmed() {
        checkSession()
    }

    fun onLoggedOut() {
        checkSession()
    }
}