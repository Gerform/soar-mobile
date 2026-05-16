package tech.soc.soar.shared.domain.auth.model

data class AuthTokens(
    val accessToken: String,
    val refreshToken: String,
    val tokenType: String
)