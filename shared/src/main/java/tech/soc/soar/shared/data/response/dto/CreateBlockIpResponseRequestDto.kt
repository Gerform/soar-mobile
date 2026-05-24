package tech.soc.soar.shared.data.response.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateBlockIpResponseRequestDto(
    @SerialName("alert_id")
    val alertId: Long,

    @SerialName("ip")
    val ip: String,

    @SerialName("field_name")
    val fieldName: String,

    @SerialName("message")
    val message: String
)