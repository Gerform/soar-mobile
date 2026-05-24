package tech.soc.soar.shared.data.response.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class ResponseRequestDto(
    @SerialName("id")
    val id: Long,

    @SerialName("alert_id")
    val alertId: Long,

    @SerialName("created_by_user")
    val createdByUser: String,

    @SerialName("space_name")
    val spaceName: String,

    @SerialName("action_name")
    val actionName: String,

    @SerialName("status")
    val status: String,

    @SerialName("target_value")
    val targetValue: String,

    @SerialName("request_body")
    val requestBody: JsonObject? = null,

    @SerialName("message")
    val message: String,

    @SerialName("created_at")
    val createdAt: String,

    @SerialName("updated_at")
    val updatedAt: String
)