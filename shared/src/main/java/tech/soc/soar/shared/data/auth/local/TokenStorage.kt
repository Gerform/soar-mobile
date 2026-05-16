package tech.soc.soar.shared.data.auth.local

import tech.soc.soar.shared.domain.auth.model.AuthTokens

interface TokenStorage {

    suspend fun saveTokens(tokens: AuthTokens)

    suspend fun getAccessToken(): String?

    suspend fun getRefreshToken(): String?

    suspend fun getTokenType(): String?

    suspend fun updateAccessToken(
        accessToken: String,
        tokenType: String
    )

    suspend fun clear()
}