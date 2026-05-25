package tech.soc.soar.presentation.alertdetails

import tech.soc.soar.shared.domain.alert.model.AlertDetails
import tech.soc.soar.shared.domain.response.model.ResponseTarget
import tech.soc.soar.shared.domain.response.model.SuccessfulAction

data class AlertDetailsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isUpdatingStatus: Boolean = false,
    val isCreatingResponse: Boolean = false,
    val canCreateResponses: Boolean = false,
    val details: AlertDetails? = null,
    val pendingResponseAction: PendingResponseAction? = null,
    val responseStatusMessage: String? = null,
    val error: String? = null,
    val successfulActions: List<SuccessfulAction> = emptyList(),
)