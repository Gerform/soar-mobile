package tech.soc.soar.presentation.responses

sealed interface ResponsesEvent {
    data object HomeClicked : ResponsesEvent
    data object BackClicked : ResponsesEvent
    data object RefreshTriggered : ResponsesEvent
    data object NextPageClicked : ResponsesEvent
    data object PreviousPageClicked : ResponsesEvent

    data class DecisionSelected(
        val responseRequestId: Long,
        val decision: String
    ) : ResponsesEvent

    data class PushRefreshReceived(
        val scrollToTop: Boolean = true
    ) : ResponsesEvent
}