package tech.soc.soar.presentation.space

sealed interface SpaceEvent {
    data object HomeClicked : SpaceEvent
    data object LogoutClicked : SpaceEvent
}