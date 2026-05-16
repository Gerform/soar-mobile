package tech.soc.soar.shared.core.network

interface TokenProvider {

    suspend fun getAccessToken(): String?

    suspend fun getTokenType(): String?
}