package tech.soc.soar.shared.domain.auth.model

data class AuthSession(
    val tokens: AuthTokens,
    val roles: List<String>,
    val user: CurrentUser? = null,
    val needTwoFactor: Boolean
)