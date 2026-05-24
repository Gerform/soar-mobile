package tech.soc.soar.shared.domain.response.model

data class ResponseRequestsPage(
    val total: Int,
    val skip: Int,
    val limit: Int,
    val responseRequests: List<ResponseRequest>,
    val fromCache: Boolean
) {
    val hasNextPage: Boolean
        get() = skip + limit < total
}