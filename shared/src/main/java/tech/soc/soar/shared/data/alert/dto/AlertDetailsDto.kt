package tech.soc.soar.shared.data.alert.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull

@Serializable
data class AlertDetailsDto(
    @SerialName("id")
    val id: Long,

    @SerialName("date")
    val date: String,

    @SerialName("alert_body")
    val alertBody: JsonObject? = null,

    @SerialName("status")
    val status: String,

    @SerialName("space_name")
    val spaceName: String
) {
    val rawBody: String
        get() {
            return (alertBody?.get("raw_body") as? JsonPrimitive)
                ?.contentOrNull
                .orEmpty()
        }
}