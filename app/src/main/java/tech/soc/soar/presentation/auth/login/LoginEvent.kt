package tech.soc.soar.presentation.auth.login

sealed interface LoginEvent {

    data class UsernameChanged(
        val value: String
    ) : LoginEvent

    data class PasswordChanged(
        val value: String
    ) : LoginEvent

    data object Submit : LoginEvent
}