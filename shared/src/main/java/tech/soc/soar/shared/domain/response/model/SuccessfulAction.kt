package tech.soc.soar.shared.domain.response.model

data class SuccessfulAction(
    val id: Long,
    val alertId: Long,
    val responseRequestId: Long,
    val approvedUser: String,
    val spaceName: String,
    val actionName: String,
    val targetValue: String,
    val createdAt: String
)