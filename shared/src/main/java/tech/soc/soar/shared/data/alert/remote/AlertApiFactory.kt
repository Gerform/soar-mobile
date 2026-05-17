package tech.soc.soar.shared.data.alert.remote

import tech.soc.soar.shared.core.network.ApiConfig
import tech.soc.soar.shared.core.network.HttpClientFactory
import tech.soc.soar.shared.core.network.TokenProvider

object AlertApiFactory {

    fun create(
        apiConfig: ApiConfig,
        tokenProvider: TokenProvider
    ): AlertApi {
        return KtorAlertApi(
            client = HttpClientFactory.create(),
            apiConfig = apiConfig,
            tokenProvider = tokenProvider
        )
    }
}