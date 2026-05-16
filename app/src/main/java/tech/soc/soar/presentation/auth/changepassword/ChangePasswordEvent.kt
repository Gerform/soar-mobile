package tech.soc.soar.presentation.auth.changepassword

sealed interface ChangePasswordEvent {

    data class OldPasswordChanged(
        val value: String
    ) : ChangePasswordEvent

    data class NewPasswordChanged(
        val value: String
    ) : ChangePasswordEvent

    data object Submit : ChangePasswordEvent

    data object BackClicked : ChangePasswordEvent
}