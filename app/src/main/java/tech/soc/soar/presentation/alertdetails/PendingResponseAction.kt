package tech.soc.soar.presentation.alertdetails

data class PendingResponseAction(
    val type: String,
    val title: String,
    val targetValue: String,
    val fieldName: String? = null
)