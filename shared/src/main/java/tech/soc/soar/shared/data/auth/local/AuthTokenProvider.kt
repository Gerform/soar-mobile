package tech.soc.soar.shared.data.auth.local

import tech.soc.soar.shared.core.network.TokenProvider

class AuthTokenProvider(
    private val tokenStorage: TokenStorage
) : TokenProvider {

    override suspend fun getAccessToken(): String? {
        return tokenStorage.getAccessToken()
    }

    override suspend fun getTokenType(): String? {
        return tokenStorage.getTokenType()
    }
}