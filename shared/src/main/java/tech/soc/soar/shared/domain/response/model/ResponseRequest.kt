package tech.soc.soar.shared.domain.response.model

data class ResponseRequest(
    val id: Long,
    val alertId: Long,
    val createdByUser: String,
    val spaceName: String,
    val actionName: String,
    val status: String,
    val targetValue: String,
    val message: String,
    val createdAt: String,
    val updatedAt: String
)