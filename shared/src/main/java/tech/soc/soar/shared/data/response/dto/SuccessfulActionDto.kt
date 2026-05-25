package tech.soc.soar.shared.data.response.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuccessfulActionDto(
    @SerialName("id")
    val id: Long,

    @SerialName("alert_id")
    val alertId: Long,

    @SerialName("response_request_id")
    val responseRequestId: Long,

    @SerialName("approved_user")
    val approvedUser: String,

    @SerialName("space_name")
    val spaceName: String,

    @SerialName("action_name")
    val actionName: String,

    @SerialName("target_value")
    val targetValue: String,

    @SerialName("created_at")
    val createdAt: String
)