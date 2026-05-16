package tech.soc.soar.shared.domain.auth.repository

import tech.soc.soar.shared.domain.auth.model.AuthSession
import tech.soc.soar.shared.domain.auth.model.CurrentUser
import tech.soc.soar.shared.domain.auth.model.SessionState

interface SessionRepository {

    suspend fun saveSession(session: AuthSession)

    suspend fun getSession(): AuthSession?

    suspend fun updateAccessToken(
        accessToken: String,
        tokenType: String
    )

    suspend fun saveCurrentUser(user: CurrentUser)

    suspend fun getCurrentUser(): CurrentUser?

    suspend fun getRefreshToken(): String?

    suspend fun getSessionState(): SessionState

    suspend fun clearSession()
}