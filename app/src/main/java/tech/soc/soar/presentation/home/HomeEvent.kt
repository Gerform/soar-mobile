package tech.soc.soar.presentation.home

sealed interface HomeEvent {
    data object LogoutClicked : HomeEvent
    data object ChangePasswordClicked : HomeEvent
}