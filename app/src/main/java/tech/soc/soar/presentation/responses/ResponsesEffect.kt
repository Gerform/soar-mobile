package tech.soc.soar.presentation.responses

sealed interface ResponsesEffect {
    data object NavigateToHome : ResponsesEffect
    data object NavigateBack : ResponsesEffect

    data class AlertStatusUpdated(
        val alertId: Long,
        val status: String
    ) : ResponsesEffect
}