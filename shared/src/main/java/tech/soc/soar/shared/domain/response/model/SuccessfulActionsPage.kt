package tech.soc.soar.shared.domain.response.model

data class SuccessfulActionsPage(
    val total: Int,
    val skip: Int,
    val limit: Int,
    val successfulActions: List<SuccessfulAction>,
    val fromCache: Boolean
)