package tech.soc.soar.shared.data.response.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class SuccessfulActionsPageDto(
    @SerialName("total")
    val total: Int,

    @SerialName("skip")
    val skip: Int,

    @SerialName("limit")
    val limit: Int,

    @SerialName("successful_actions")
    val successfulActions: List<SuccessfulActionDto>
)