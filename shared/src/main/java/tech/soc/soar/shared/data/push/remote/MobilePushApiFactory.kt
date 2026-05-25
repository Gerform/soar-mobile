package tech.soc.soar.shared.data.push.remote

import tech.soc.soar.shared.core.network.ApiConfig
import tech.soc.soar.shared.core.network.HttpClientFactory
import tech.soc.soar.shared.core.network.TokenProvider

object MobilePushApiFactory {

    fun create(
        apiConfig: ApiConfig,
        tokenProvider: TokenProvider
    ): MobilePushApi {
        return KtorMobilePushApi(
            client = HttpClientFactory.create(),
            apiConfig = apiConfig,
            tokenProvider = tokenProvider
        )
    }
}