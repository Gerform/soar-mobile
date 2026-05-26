package tech.soc.soar.presentation.auth.twofactor

sealed interface TwoFactorEvent {

    data class CodeChanged(
        val value: String
    ) : TwoFactorEvent

    data object Submit : TwoFactorEvent

    data object BackClicked : TwoFactorEvent
}