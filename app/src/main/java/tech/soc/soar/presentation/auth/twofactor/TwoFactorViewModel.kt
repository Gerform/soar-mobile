package tech.soc.soar.presentation.auth.twofactor

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.usecase.ConfirmTwoFactorUseCase

class TwoFactorViewModel(
    private val confirmTwoFactorUseCase: ConfirmTwoFactorUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(TwoFactorUiState())
    val state: StateFlow<TwoFactorUiState> = _state.asStateFlow()

    private val _effect = Channel<TwoFactorEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: TwoFactorEvent) {
        when (event) {
            is TwoFactorEvent.CodeChanged -> {
                _state.update {
                    it.copy(
                        code = event.value,
                        codeError = null,
                        generalError = null
                    )
                }
            }

            TwoFactorEvent.Submit -> {
                confirmCode()
            }

            TwoFactorEvent.BackClicked -> {
                viewModelScope.launch {
                    _effect.send(TwoFactorEffect.NavigateToLogin)
                }
            }
        }
    }

    private fun confirmCode() {
        val currentState = state.value

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    codeError = null,
                    generalError = null
                )
            }

            when (
                val result = confirmTwoFactorUseCase(
                    code = currentState.code
                )
            ) {
                is AppResult.Error -> {
                    handleError(result.error)
                }

                is AppResult.Success -> {
                    _state.update {
                        it.copy(isLoading = false)
                    }

                    _effect.send(TwoFactorEffect.NavigateToHome)
                }
            }
        }
    }

    private fun handleError(error: AppError) {
        when (error) {
            is AppError.Validation -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        codeError = error.message
                    )
                }
            }

            else -> {
                _state.update {
                    it.copy(
                        isLoading = false,
                        generalError = error.toUserMessage()
                    )
                }
            }
        }
    }

    private fun AppError.toUserMessage(): String {
        return message
    }
}