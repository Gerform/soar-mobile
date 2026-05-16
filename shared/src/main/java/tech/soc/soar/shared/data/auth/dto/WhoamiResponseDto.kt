package tech.soc.soar.shared.data.auth.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class WhoamiResponseDto(
    @SerialName("uid")
    val uid: Int,

    @SerialName("username")
    val username: String,

    @SerialName("mail")
    val mail: String,

    @SerialName("roles")
    val roles: List<String> = emptyList(),

    @SerialName("last_conn")
    val lastConnection: String? = null,

    @SerialName("two_factor_secret")
    val twoFactorSecret: String? = null,

    @SerialName("two_factor_confirmed")
    val twoFactorConfirmed: Boolean
)