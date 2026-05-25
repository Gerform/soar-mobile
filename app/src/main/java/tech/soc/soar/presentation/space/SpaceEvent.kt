package tech.soc.soar.presentation.space

sealed interface SpaceEvent {
    data object HomeClicked : SpaceEvent
    data object LogoutClicked : SpaceEvent
    data object NextPageClicked : SpaceEvent
    data object PreviousPageClicked : SpaceEvent
    data object RefreshTriggered : SpaceEvent

    data class AlertClicked(
        val alertId: Long
    ) : SpaceEvent

    data class AlertStatusChanged(
        val alertId: Long,
        val status: String
    ) : SpaceEvent

    data class PushRefreshReceived(
        val scrollToTop: Boolean = true
    ) : SpaceEvent
}