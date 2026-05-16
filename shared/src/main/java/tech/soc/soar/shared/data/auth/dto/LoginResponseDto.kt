package tech.soc.soar.shared.data.auth.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginResponseDto(
    @SerialName("access_token")
    val accessToken: String,

    @SerialName("token_type")
    val tokenType: String,

    @SerialName("refresh_token")
    val refreshToken: String,

    @SerialName("roles")
    val roles: List<String> = emptyList(),

    @SerialName("need_2fa")
    val needTwoFactor: Boolean
)