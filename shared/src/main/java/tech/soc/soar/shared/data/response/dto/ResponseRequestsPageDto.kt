package tech.soc.soar.shared.data.response.dto

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ResponseRequestsPageDto(
    @SerialName("total")
    val total: Int,

    @SerialName("skip")
    val skip: Int,

    @SerialName("limit")
    val limit: Int,

    @SerialName("response_requests")
    val responseRequests: List<ResponseRequestDto>
)