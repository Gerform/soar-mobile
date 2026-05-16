package tech.soc.soar.shared.data.auth.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RefreshTokenResponseDto(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("token_type")
    val tokenType: String
)