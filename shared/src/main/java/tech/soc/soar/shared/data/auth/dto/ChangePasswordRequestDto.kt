package tech.soc.soar.shared.data.auth.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequestDto(
    @SerialName("old_password")
    val oldPassword: String,

    @SerialName("new_password")
    val newPassword: String
)