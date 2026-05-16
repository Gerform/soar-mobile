package tech.soc.soar.shared.data.auth.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class VerifyTwoFactorCodeResponseDto(
    @SerialName("status")
    val status: String,

    @SerialName("new_access_token")
    val newAccessToken: String
)