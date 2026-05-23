package tech.soc.soar.shared.data.alert.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import kotlinx.serialization.SerializationException
import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.network.ApiConfig
import tech.soc.soar.shared.core.network.ApiErrorMapper
import tech.soc.soar.shared.core.network.TokenProvider
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.data.alert.dto.AlertDetailsDto
import tech.soc.soar.shared.data.alert.dto.AlertDto
import java.io.IOException

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
        } catch (exception: IOException) {
            AppResult.Error(
                AppError.Network(
                    message = "Failed to connect to server"
                )
            )
        } catch (exception: SerializationException) {
            Log.e("KtorAlertApi", "Failed to parse alerts response", exception)

            AppResult.Error(
                AppError.Api(
                    message = "Failed to parse server response"
                )
            )
        } catch (exception: Exception) {
            Log.e("KtorAlertApi", "Unexpected alerts error", exception)

            AppResult.Error(
                AppError.Api(
                    message = exception.message ?: "Unexpected error"
                )
            )
        }
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
        } catch (exception: IOException) {
            AppResult.Error(
                AppError.Network(
                    message = "Failed to connect to server"
                )
            )
        } catch (exception: SerializationException) {
            Log.e("KtorAlertApi", "Failed to parse alert details response", exception)

            AppResult.Error(
                AppError.Api(
                    message = "Failed to parse server response"
                )
            )
        } catch (exception: Exception) {
            Log.e("KtorAlertApi", "Unexpected alert details error", exception)

            AppResult.Error(
                AppError.Api(
                    message = exception.message ?: "Unexpected error"
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
}