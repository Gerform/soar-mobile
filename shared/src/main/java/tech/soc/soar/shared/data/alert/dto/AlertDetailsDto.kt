package tech.soc.soar.shared.data.alert.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
data class AlertDetailsDto(
    @SerialName("id")
    val id: Long,

    @SerialName("date")
    val date: String,

    @SerialName("alert_body")
    val alertBody: JsonObject? = null,

    @SerialName("raw_body")
    val rawBody: String,

    @SerialName("status")
    val status: String,

    @SerialName("space_name")
    val spaceName: String
)