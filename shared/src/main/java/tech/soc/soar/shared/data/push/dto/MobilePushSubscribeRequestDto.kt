package tech.soc.soar.shared.data.push.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class MobilePushSubscribeRequestDto(
    @SerialName("token")
    val token: String,

    @SerialName("platform")
    val platform: String,

    @SerialName("device_id")
    val deviceId: String,

    @SerialName("device_name")
    val deviceName: String
)