package tech.soc.soar.shared.domain.alert.model

import tech.soc.soar.shared.domain.response.model.ResponseTarget

data class AlertDetails(
    val id: Long,
    val date: String,
    val status: String,
    val spaceName: String,
    val rawBody: String,
    val responseTargets: List<ResponseTarget> = emptyList(),
    val fromCache: Boolean
)