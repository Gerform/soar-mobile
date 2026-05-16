package tech.soc.soar.shared.domain.auth.model

sealed interface LoginResult {

    data class Success(
        val session: AuthSession
    ) : LoginResult

    data class TwoFactorRequired(
        val session: AuthSession
    ) : LoginResult
}