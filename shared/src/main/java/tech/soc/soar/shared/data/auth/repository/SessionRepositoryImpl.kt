package tech.soc.soar.shared.data.auth.repository

import tech.soc.soar.shared.data.auth.local.TokenStorage
import tech.soc.soar.shared.data.auth.local.UserSessionCache
import tech.soc.soar.shared.domain.auth.model.AuthSession
import tech.soc.soar.shared.domain.auth.model.AuthTokens
import tech.soc.soar.shared.domain.auth.model.CurrentUser
import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.repository.SessionRepository

class SessionRepositoryImpl(
    private val tokenStorage: TokenStorage,
    private val userSessionCache: UserSessionCache
) : SessionRepository {

    override suspend fun saveSession(session: AuthSession) {
        tokenStorage.saveTokens(session.tokens)
        tokenStorage.saveRoles(session.roles)
        tokenStorage.saveNeedTwoFactor(session.needTwoFactor)

        userSessionCache.saveRoles(session.roles)
        userSessionCache.saveNeedTwoFactor(session.needTwoFactor)

        session.user?.let { user ->
            userSessionCache.saveUser(user)
        }
    }

    override suspend fun getSession(): AuthSession? {
        val accessToken = tokenStorage.getAccessToken()
        val refreshToken = tokenStorage.getRefreshToken()
        val tokenType = tokenStorage.getTokenType()

        if (
            accessToken.isNullOrBlank() ||
            refreshToken.isNullOrBlank() ||
            tokenType.isNullOrBlank()
        ) {
            return null
        }

        val roles = tokenStorage.getRoles()
        val needTwoFactor = tokenStorage.getNeedTwoFactor()

        return AuthSession(
            tokens = AuthTokens(
                accessToken = accessToken,
                refreshToken = refreshToken,
                tokenType = tokenType
            ),
            roles = roles,
            user = userSessionCache.getUser(),
            needTwoFactor = needTwoFactor
        )
    }

    override suspend fun updateAccessToken(
        accessToken: String,
        tokenType: String
    ) {
        tokenStorage.updateAccessToken(
            accessToken = accessToken,
            tokenType = tokenType
        )
    }

    override suspend fun saveCurrentUser(user: CurrentUser) {
        userSessionCache.saveUser(user)
        userSessionCache.saveRoles(user.roles)

        tokenStorage.saveRoles(user.roles)
    }

    override suspend fun getCurrentUser(): CurrentUser? {
        return userSessionCache.getUser()
    }

    override suspend fun getRefreshToken(): String? {
        return tokenStorage.getRefreshToken()
    }

    override suspend fun getSessionState(): SessionState {
        val session = getSession()
            ?: return SessionState.Unauthenticated

        return if (session.needTwoFactor) {
            SessionState.RequiresTwoFactor(session)
        } else {
            SessionState.Authenticated(session)
        }
    }

    override suspend fun clearSession() {
        tokenStorage.clear()
        userSessionCache.clear()
    }
}