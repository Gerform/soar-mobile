package tech.soc.soar.shared.domain.alert.model

data class AlertDetails(
    val id: Long,
    val date: String,
    val status: String,
    val spaceName: String,
    val rawBody: String,
    val fromCache: Boolean
)