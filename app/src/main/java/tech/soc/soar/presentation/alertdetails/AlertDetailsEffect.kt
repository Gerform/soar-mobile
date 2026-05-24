package tech.soc.soar.presentation.alertdetails

sealed interface AlertDetailsEffect {
    data object NavigateToHome : AlertDetailsEffect
    data object NavigateBack : AlertDetailsEffect
    data object NavigateToResponses : AlertDetailsEffect

    data class AlertStatusUpdated(
        val alertId: Long,
        val status: String
    ) : AlertDetailsEffect
}