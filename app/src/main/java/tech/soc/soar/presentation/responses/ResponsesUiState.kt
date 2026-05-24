package tech.soc.soar.presentation.responses

import tech.soc.soar.shared.domain.response.model.ResponseRequest

data class ResponsesUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val responses: List<ResponseRequest> = emptyList(),
    val page: Int = 0,
    val pageSize: Int = 50,
    val total: Int = 0,
    val fromCache: Boolean = false,
    val error: String? = null
) {
    val hasNextPage: Boolean
        get() = (page + 1) * pageSize < total

    val showPagination: Boolean
        get() = page > 0 || hasNextPage
}