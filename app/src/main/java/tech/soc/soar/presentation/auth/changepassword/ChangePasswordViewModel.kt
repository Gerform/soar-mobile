package tech.soc.soar.presentation.auth.changepassword

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.receiveAsFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.usecase.ChangePasswordUseCase

class ChangePasswordViewModel(
    private val changePasswordUseCase: ChangePasswordUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(ChangePasswordUiState())
    val state: StateFlow<ChangePasswordUiState> = _state.asStateFlow()

    private val _effect = Channel<ChangePasswordEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: ChangePasswordEvent) {
        when (event) {
            is ChangePasswordEvent.OldPasswordChanged -> {
                _state.update {
                    it.copy(
                        oldPassword = event.value,
                        oldPasswordError = null,
                        generalError = null,
                        successMessage = null
                    )
                }
            }

            is ChangePasswordEvent.NewPasswordChanged -> {
                _state.update {
                    it.copy(
                        newPassword = event.value,
                        newPasswordError = null,
                        generalError = null,
                        successMessage = null
                    )
                }
            }

            ChangePasswordEvent.Submit -> {
                changePassword()
            }

            ChangePasswordEvent.BackClicked -> {
                viewModelScope.launch {
                    _effect.send(ChangePasswordEffect.NavigateBack)
                }
            }
        }
    }

    private fun changePassword() {
        val currentState = state.value

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    oldPasswordError = null,
                    newPasswordError = null,
                    generalError = null,
                    successMessage = null
                )
            }

            when (
                val result = changePasswordUseCase(
                    oldPassword = currentState.oldPassword,
                    newPassword = currentState.newPassword
                )
            ) {
                is AppResult.Success -> {
                    _state.update {
                        it.copy(
                            isLoading = false,
                            oldPassword = "",
                            newPassword = "",
                            successMessage = result.data.status.ifBlank {
                                "Password changed successfully"
                            }
                        )
                    }

                    delay(800)
                    _effect.send(ChangePasswordEffect.NavigateBack)
                }

                is AppResult.Error -> {
                    handleError(result.error)
                }
            }
        }
    }

    private fun handleError(error: AppError) {
        val message = error.message

        _state.update {
            when {
                "old" in message.lowercase() -> {
                    it.copy(
                        isLoading = false,
                        oldPasswordError = message
                    )
                }

                "new" in message.lowercase() || "password" in message.lowercase() -> {
                    it.copy(
                        isLoading = false,
                        newPasswordError = message
                    )
                }

                else -> {
                    it.copy(
                        isLoading = false,
                        generalError = message
                    )
                }
            }
        }
    }
}