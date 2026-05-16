package tech.soc.soar.presentation.auth.login

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
import tech.soc.soar.shared.domain.auth.model.LoginResult
import tech.soc.soar.shared.domain.auth.usecase.LoginUseCase

class LoginViewModel(
    private val loginUseCase: LoginUseCase
) : ViewModel() {

    private val _state = MutableStateFlow(LoginUiState())
    val state: StateFlow<LoginUiState> = _state.asStateFlow()

    private val _effect = Channel<LoginEffect>()
    val effect = _effect.receiveAsFlow()

    fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.UsernameChanged -> {
                _state.update {
                    it.copy(
                        username = event.value,
                        usernameError = null,
                        generalError = null
                    )
                }
            }

            is LoginEvent.PasswordChanged -> {
                _state.update {
                    it.copy(
                        password = event.value,
                        passwordError = null,
                        generalError = null
                    )
                }
            }

            LoginEvent.Submit -> {
                login()
            }
        }
    }

    private fun login() {
        val currentState = state.value

        val usernameError = if (currentState.username.isBlank()) {
            "Username is required"
        } else {
            null
        }

        val passwordError = if (currentState.password.isBlank()) {
            "Password is required"
        } else {
            null
        }

        if (usernameError != null || passwordError != null) {
            _state.update {
                it.copy(
                    usernameError = usernameError,
                    passwordError = passwordError,
                    generalError = null,
                    isLoading = false
                )
            }

            return
        }

        viewModelScope.launch {
            _state.update {
                it.copy(
                    isLoading = true,
                    usernameError = null,
                    passwordError = null,
                    generalError = null
                )
            }

            when (
                val result = loginUseCase(
                    username = currentState.username,
                    password = currentState.password
                )
            ) {
                is AppResult.Error -> {
                    handleError(result.error)
                }

                is AppResult.Success -> {
                    _state.update {
                        it.copy(isLoading = false)
                    }

                    when (result.data) {
                        is LoginResult.Success -> {
                            _effect.send(LoginEffect.NavigateToHome)
                        }

                        is LoginResult.TwoFactorRequired -> {
                            _effect.send(LoginEffect.NavigateToTwoFactor)
                        }
                    }
                }
            }
        }
    }
    private fun handleError(error: AppError) {
        when (error) {
            is AppError.Validation -> {
                val message = error.message.lowercase()

                _state.update {
                    when {
                        "username" in message -> {
                            it.copy(
                                isLoading = false,
                                usernameError = error.message
                            )
                        }

                        "password" in message -> {
                            it.copy(
                                isLoading = false,
                                passwordError = error.message
                            )
                        }

                        else -> {
                            it.copy(
                                isLoading = false,
                                generalError = error.message
                            )
                        }
                    }
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