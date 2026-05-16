package tech.soc.soar.shared.data.auth.remote

import tech.soc.soar.shared.core.network.ApiConfig
import tech.soc.soar.shared.core.network.HttpClientFactory
import tech.soc.soar.shared.core.network.TokenProvider

object AuthApiFactory {

    fun create(
        apiConfig: ApiConfig,
        tokenProvider: TokenProvider
    ): AuthApi {
        val httpClient = HttpClientFactory.create()

        return KtorAuthApi(
            client = httpClient,
            apiConfig = apiConfig,
            tokenProvider = tokenProvider
        )
    }
}