package tech.soc.soar.shared.data.auth.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class OperationStatusDto(
    @SerialName("status")
    val status: String
)