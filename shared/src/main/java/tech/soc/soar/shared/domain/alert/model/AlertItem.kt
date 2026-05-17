package tech.soc.soar.shared.domain.alert.model

data class AlertItem(
    val id: Long,
    val date: String,
    val status: String,
    val spaceName: String,
    val isViewed: Boolean,
    val reason: String
)