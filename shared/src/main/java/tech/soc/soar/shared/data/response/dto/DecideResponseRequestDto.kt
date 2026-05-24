package tech.soc.soar.shared.data.response.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DecideResponseRequestDto(
    @SerialName("decision")
    val decision: String
)