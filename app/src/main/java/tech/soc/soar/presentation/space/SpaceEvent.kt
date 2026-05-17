package tech.soc.soar.presentation.space

sealed interface SpaceEvent {
    data object HomeClicked : SpaceEvent
    data object LogoutClicked : SpaceEvent
    data object NextPageClicked : SpaceEvent
    data object PreviousPageClicked : SpaceEvent

    data class AlertClicked(
        val alertId: Long
    ) : SpaceEvent
}