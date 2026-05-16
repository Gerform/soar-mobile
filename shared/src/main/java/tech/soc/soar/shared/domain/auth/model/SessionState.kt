package tech.soc.soar.shared.domain.auth.model

sealed interface SessionState {

    data object Loading : SessionState

    data object Unauthenticated : SessionState

    data class RequiresTwoFactor(
        val session: AuthSession
    ) : SessionState

    data class Authenticated(
        val session: AuthSession
    ) : SessionState
}