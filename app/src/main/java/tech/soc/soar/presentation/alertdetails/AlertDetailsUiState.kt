package tech.soc.soar.presentation.alertdetails

import tech.soc.soar.shared.domain.alert.model.AlertDetails

data class AlertDetailsUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isUpdatingStatus: Boolean = false,
    val details: AlertDetails? = null,
    val error: String? = null
)