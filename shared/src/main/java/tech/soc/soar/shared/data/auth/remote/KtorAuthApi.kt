package tech.soc.soar.shared.data.auth.remote

import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.bearerAuth
import io.ktor.client.request.header
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType
import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.network.ApiConfig
import tech.soc.soar.shared.core.network.ApiErrorMapper
import tech.soc.soar.shared.core.network.TokenProvider
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.data.auth.dto.ChangePasswordRequestDto
import tech.soc.soar.shared.data.auth.dto.LoginRequestDto
import tech.soc.soar.shared.data.auth.dto.LoginResponseDto
import tech.soc.soar.shared.data.auth.dto.LogoutRequestDto
import tech.soc.soar.shared.data.auth.dto.OperationStatusDto
import tech.soc.soar.shared.data.auth.dto.RefreshTokenRequestDto
import tech.soc.soar.shared.data.auth.dto.RefreshTokenResponseDto
import tech.soc.soar.shared.data.auth.dto.VerifyTwoFactorCodeRequestDto
import tech.soc.soar.shared.data.auth.dto.VerifyTwoFactorCodeResponseDto
import tech.soc.soar.shared.data.auth.dto.WhoamiResponseDto

class KtorAuthApi(
    private val client: HttpClient,
    private val apiConfig: ApiConfig,
    private val tokenProvider: TokenProvider
) : AuthApi {

    override suspend fun login(
        request: LoginRequestDto
    ): AppResult<LoginResponseDto> {
        return safePost(
            path = "/authentication/login",
            body = request,
            authorized = false
        )
    }

    override suspend fun refreshToken(
        request: RefreshTokenRequestDto
    ): AppResult<RefreshTokenResponseDto> {
        return safePost(
            path = "/authentication/refresh",
            body = request,
            authorized = false
        )
    }

    override suspend fun logout(
        request: LogoutRequestDto
    ): AppResult<OperationStatusDto> {
        return safePost(
            path = "/authentication/logout",
            body = request,
            authorized = false
        )
    }

    override suspend fun whoami(): AppResult<WhoamiResponseDto> {
        return safePostWithoutBody(
            path = "/authentication/whoami",
            authorized = true
        )
    }

    override suspend fun changePassword(
        request: ChangePasswordRequestDto
    ): AppResult<OperationStatusDto> {
        return safePost(
            path = "/authentication/change_password",
            body = request,
            authorized = true
        )
    }

    override suspend fun verifyTwoFactorCode(
        request: VerifyTwoFactorCodeRequestDto
    ): AppResult<VerifyTwoFactorCodeResponseDto> {
        return safePost(
            path = "/Two-Factor/verify_two_factor_code",
            body = request,
            authorized = true
        )
    }

    private suspend inline fun <reified Request : Any, reified Response : Any> safePost(
        path: String,
        body: Request,
        authorized: Boolean
    ): AppResult<Response> {
        return try {
            val response = client.post(buildUrl(path)) {
                contentType(ContentType.Application.Json)

                if (authorized) {
                    addAuthHeader()
                }

                setBody(body)
            }

            if (response.status.value in 200..299) {
                AppResult.Success(response.body())
            } else {
                AppResult.Error(ApiErrorMapper.map(response))
            }
        } catch (exception: Exception) {
            AppResult.Error(
                AppError.Network(
                    message = exception.message ?: "Network error"
                )
            )
        }
    }

    private suspend inline fun <reified Response : Any> safePostWithoutBody(
        path: String,
        authorized: Boolean
    ): AppResult<Response> {
        return try {
            val response = client.post(buildUrl(path)) {
                contentType(ContentType.Application.Json)

                if (authorized) {
                    addAuthHeader()
                }
            }

            if (response.status.value in 200..299) {
                AppResult.Success(response.body())
            } else {
                AppResult.Error(ApiErrorMapper.map(response))
            }
        } catch (exception: Exception) {
            AppResult.Error(
                AppError.Network(
                    message = exception.message ?: "Network error"
                )
            )
        }
    }

    private suspend fun io.ktor.client.request.HttpRequestBuilder.addAuthHeader() {
        val accessToken = tokenProvider.getAccessToken()

        if (!accessToken.isNullOrBlank()) {
            bearerAuth(accessToken)
        }
    }

    private fun buildUrl(path: String): String {
        return apiConfig.baseUrl.trimEnd('/') + path
    }
}