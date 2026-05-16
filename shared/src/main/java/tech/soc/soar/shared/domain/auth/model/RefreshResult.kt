package tech.soc.soar.shared.domain.auth.model

data class RefreshResult(
    val accessToken: String,
    val tokenType: String
)