package tech.soc.soar.shared.data.push.remote

import android.util.Log
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.RedirectResponseException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.network.ApiConfig
import tech.soc.soar.shared.core.network.ApiErrorMapper
import tech.soc.soar.shared.core.network.TokenProvider
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.data.push.dto.MobilePushSubscribeRequestDto
import java.io.IOException

class KtorMobilePushApi(
    private val client: HttpClient,
    private val apiConfig: ApiConfig,
    private val tokenProvider: TokenProvider
) : MobilePushApi {

    override suspend fun subscribe(
        token: String,
        deviceId: String,
        deviceName: String
    ): AppResult<String> {
        return try {
            val response = client.post(buildUrl("/mobile-push/subscribe")) {
                val accessToken = tokenProvider.getAccessToken()

                if (!accessToken.isNullOrBlank()) {
                    bearerAuth(accessToken)
                }

                contentType(ContentType.Application.Json)

                setBody(
                    MobilePushSubscribeRequestDto(
                        token = token,
                        platform = PLATFORM_ANDROID,
                        deviceId = deviceId,
                        deviceName = deviceName
                    )
                )
            }

            if (response.status.value in SUCCESS_STATUS_RANGE) {
                val body = response.body<JsonObject>()

                val status = (body["status"] as? JsonPrimitive)
                    ?.contentOrNull
                    ?: "Mobile push token registered"

                AppResult.Success(status)
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
            Log.e("KtorMobilePushApi", "Failed to parse mobile push response", exception)

            AppResult.Error(
                AppError.Api(
                    message = "Failed to parse server response"
                )
            )
        } catch (exception: Exception) {
            Log.e("KtorMobilePushApi", "Unexpected mobile push error", exception)

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
        const val PLATFORM_ANDROID = "android"
        val SUCCESS_STATUS_RANGE = 200..299
    }
}