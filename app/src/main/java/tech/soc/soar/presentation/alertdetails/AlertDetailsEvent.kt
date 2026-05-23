package tech.soc.soar.presentation.alertdetails

sealed interface AlertDetailsEvent {
    data object HomeClicked : AlertDetailsEvent
    data object BackClicked : AlertDetailsEvent

    data class StatusSelected(
        val status: String
    ) : AlertDetailsEvent
}