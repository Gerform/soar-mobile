package tech.soc.soar.presentation.space

import tech.soc.soar.shared.domain.alert.model.AlertItem

data class SpaceUiState(
    val isLoading: Boolean = false,
    val alerts: List<AlertItem> = emptyList(),
    val page: Int = 0,
    val hasNextPage: Boolean = false,
    val fromCache: Boolean = false,
    val error: String? = null
) {
    val showPagination: Boolean
        get() = page > 0 || hasNextPage
}