package tech.soc.soar.shared.data.response.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.get
import io.ktor.client.request.parameter
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerializationException
import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.network.ApiConfig
import tech.soc.soar.shared.core.network.ApiErrorMapper
import tech.soc.soar.shared.core.network.TokenProvider
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.data.response.dto.CreateBlockIpResponseRequestDto
import tech.soc.soar.shared.data.response.dto.CreateResponseResultDto
import tech.soc.soar.shared.data.response.dto.ResponseRequestsPageDto
import java.io.IOException

class KtorResponseApi(
    private val client: HttpClient,
    private val apiConfig: ApiConfig,
    private val tokenProvider: TokenProvider
) : ResponseApi {

    override suspend fun createBlockIpResponse(
        alertId: Long,
        ip: String,
        fieldName: String,
        message: String
    ): AppResult<CreateResponseResultDto> {
        return try {
            val response = client.post(buildUrl("/responses/block-ip")) {
                val accessToken = tokenProvider.getAccessToken()

                if (!accessToken.isNullOrBlank()) {
                    bearerAuth(accessToken)
                }

                contentType(ContentType.Application.Json)

                setBody(
                    CreateBlockIpResponseRequestDto(
                        alertId = alertId,
                        ip = ip,
                        fieldName = fieldName,
                        message = message
                    )
                )
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
            Log.e("KtorResponseApi", "Failed to parse create response result", exception)

            AppResult.Error(
                AppError.Api(
                    message = "Failed to parse server response"
                )
            )
        } catch (exception: Exception) {
            Log.e("KtorResponseApi", "Unexpected create response error", exception)

            AppResult.Error(
                AppError.Api(
                    message = exception.message ?: "Unexpected error"
                )
            )
        }
    }

    override suspend fun getResponseRequestsByAlertId(
        alertId: Long,
        skip: Int,
        limit: Int
    ): AppResult<ResponseRequestsPageDto> {
        return try {
            val response = client.get(buildUrl("/responses/$alertId/responses")) {
                val accessToken = tokenProvider.getAccessToken()

                if (!accessToken.isNullOrBlank()) {
                    bearerAuth(accessToken)
                }

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
            Log.e("KtorResponseApi", "Failed to parse response requests", exception)

            AppResult.Error(
                AppError.Api(
                    message = "Failed to parse server response"
                )
            )
        } catch (exception: Exception) {
            Log.e("KtorResponseApi", "Unexpected response requests error", exception)

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