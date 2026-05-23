package tech.soc.soar.shared.data.alert.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UpdateAlertStatusRequestDto(
    @SerialName("status")
    val status: String
)