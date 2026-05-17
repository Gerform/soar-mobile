package tech.soc.soar.shared.domain.alert.model

data class AlertsPage(
    val alerts: List<AlertItem>,
    val page: Int,
    val pageSize: Int,
    val hasNextPage: Boolean,
    val fromCache: Boolean
)