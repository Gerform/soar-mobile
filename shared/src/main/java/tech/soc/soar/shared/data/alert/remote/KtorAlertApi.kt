package tech.soc.soar.shared.data.alert.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.network.ApiConfig
import tech.soc.soar.shared.core.network.ApiErrorMapper
import tech.soc.soar.shared.core.network.TokenProvider
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.data.alert.dto.AlertDetailsDto
import tech.soc.soar.shared.data.alert.dto.AlertDto

class KtorAlertApi(
    private val client: HttpClient,
    private val apiConfig: ApiConfig,
    private val tokenProvider: TokenProvider
) : AlertApi {

    override suspend fun getAlerts(
        spaceName: String,
        skip: Int,
        limit: Int
    ): AppResult<List<AlertDto>> {
        return try {
            val response = client.get(buildUrl("/alerts")) {
                val accessToken = tokenProvider.getAccessToken()

                if (!accessToken.isNullOrBlank()) {
                    bearerAuth(accessToken)
                }

                parameter("space_name", spaceName)
                parameter("skip", skip)
                parameter("limit", limit)
            }

            if (response.status.value in SUCCESS_STATUS_RANGE) {
                AppResult.Success(response.body())
            } else {
                AppResult.Error(ApiErrorMapper.map(response))
            }
        } catch (exception: ClientRequestException) {
            AppResult.Error(ApiErrorMapper.map(exception.response))
        } catch (exception: ServerResponseException) {
            AppResult.Error(ApiErrorMapper.map(exception.response))
        } catch (exception: RedirectResponseException) {
            AppResult.Error(ApiErrorMapper.map(exception.response))
        } catch (exception: Exception) {
            AppResult.Error(
                AppError.Network(
                    message = "Failed to connect to server"
                )
            )
        }
    }

    private fun buildUrl(path: String): String {
        return apiConfig.baseUrl.trimEnd('/') + path
    }

    private companion object {
        val SUCCESS_STATUS_RANGE = 200..299
    }

    override suspend fun getAlertById(
        alertId: Long,
        spaceName: String
    ): AppResult<AlertDetailsDto> {
        return try {
            val response = client.get(buildUrl("/alerts/$alertId")) {
                val accessToken = tokenProvider.getAccessToken()

                if (!accessToken.isNullOrBlank()) {
                    bearerAuth(accessToken)
                }

                parameter("space_name", spaceName)
            }

            if (response.status.value in SUCCESS_STATUS_RANGE) {
                AppResult.Success(response.body())
            } else {
                AppResult.Error(ApiErrorMapper.map(response))
            }
        } catch (exception: ClientRequestException) {
            AppResult.Error(ApiErrorMapper.map(exception.response))
        } catch (exception: ServerResponseException) {
            AppResult.Error(ApiErrorMapper.map(exception.response))
        } catch (exception: RedirectResponseException) {
            AppResult.Error(ApiErrorMapper.map(exception.response))
        } catch (exception: Exception) {
            AppResult.Error(
                AppError.Network(
                    message = "Failed to connect to server"
                )
            )
        }
    }
}