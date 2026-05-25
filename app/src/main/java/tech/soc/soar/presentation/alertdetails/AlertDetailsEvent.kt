package tech.soc.soar.presentation.alertdetails

import tech.soc.soar.shared.domain.response.model.ResponseTarget
import tech.soc.soar.shared.domain.response.model.SuccessfulAction

sealed interface AlertDetailsEvent {
    data object HomeClicked : AlertDetailsEvent
    data object BackClicked : AlertDetailsEvent
    data object ResponsesClicked : AlertDetailsEvent
    data object RefreshTriggered : AlertDetailsEvent
    data object DismissResponseDialog : AlertDetailsEvent

    data class StatusSelected(
        val status: String
    ) : AlertDetailsEvent

    data class ResponseTargetLongPressed(
        val target: ResponseTarget
    ) : AlertDetailsEvent

    data class SuccessfulActionTargetLongPressed(
        val action: SuccessfulAction
    ) : AlertDetailsEvent

    data class CreateResponseActionConfirmed(
        val message: String
    ) : AlertDetailsEvent
}