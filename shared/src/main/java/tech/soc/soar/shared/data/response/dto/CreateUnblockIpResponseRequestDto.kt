package tech.soc.soar.shared.data.response.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateUnblockIpResponseRequestDto(
    @SerialName("alert_id")
    val alertId: Long,

    @SerialName("ip")
    val ip: String,

    @SerialName("message")
    val message: String
)