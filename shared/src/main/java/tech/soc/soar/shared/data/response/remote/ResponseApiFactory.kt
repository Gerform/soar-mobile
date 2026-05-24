package tech.soc.soar.shared.data.response.remote

import tech.soc.soar.shared.core.network.ApiConfig
import tech.soc.soar.shared.core.network.HttpClientFactory
import tech.soc.soar.shared.core.network.TokenProvider

object ResponseApiFactory {

    fun create(
        apiConfig: ApiConfig,
        tokenProvider: TokenProvider
    ): ResponseApi {
        return KtorResponseApi(
            client = HttpClientFactory.create(),
            apiConfig = apiConfig,
            tokenProvider = tokenProvider
        )
    }
}