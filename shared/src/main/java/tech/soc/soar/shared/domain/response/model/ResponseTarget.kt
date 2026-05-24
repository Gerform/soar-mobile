package tech.soc.soar.shared.domain.response.model

data class ResponseTarget(
    val fieldName: String,
    val value: String,
    val type: ResponseTargetType
)

enum class ResponseTargetType {
    IP
}