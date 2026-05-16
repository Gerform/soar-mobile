package tech.soc.soar.presentation.auth.login

import tech.soc.soar.shared.domain.account.model.SavedAccount

sealed interface LoginEvent {

    data class UsernameChanged(
        val value: String
    ) : LoginEvent

    data class PasswordChanged(
        val value: String
    ) : LoginEvent

    data object Submit : LoginEvent

    data object OtherAccountClicked : LoginEvent

    data class SavedAccountSelected(
        val account: SavedAccount
    ) : LoginEvent

    data object AddNewUserClicked : LoginEvent

    data object DismissAccountPicker : LoginEvent
}