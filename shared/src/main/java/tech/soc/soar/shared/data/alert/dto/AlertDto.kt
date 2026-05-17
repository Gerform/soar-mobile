package tech.soc.soar.shared.data.alert.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class AlertDto(
    @SerialName("id")
    val id: Long,

    @SerialName("date")
    val date: String,

    @SerialName("status")
    val status: String,

    @SerialName("space_name")
    val spaceName: String,

    @SerialName("is_viewed")
    val isViewed: Boolean,

    @SerialName("reason")
    val reason: String
)