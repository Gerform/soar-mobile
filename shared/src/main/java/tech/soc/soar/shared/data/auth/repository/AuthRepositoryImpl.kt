package tech.soc.soar.shared.data.auth.repository

import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.data.auth.dto.ChangePasswordRequestDto
import tech.soc.soar.shared.data.auth.dto.LoginRequestDto
import tech.soc.soar.shared.data.auth.dto.LogoutRequestDto
import tech.soc.soar.shared.data.auth.dto.RefreshTokenRequestDto
import tech.soc.soar.shared.data.auth.dto.VerifyTwoFactorCodeRequestDto
import tech.soc.soar.shared.data.auth.mapper.toDomain
import tech.soc.soar.shared.data.auth.remote.AuthApi
import tech.soc.soar.shared.domain.auth.model.AuthSession
import tech.soc.soar.shared.domain.auth.model.CurrentUser
import tech.soc.soar.shared.domain.auth.model.OperationStatus
import tech.soc.soar.shared.domain.auth.model.RefreshResult
import tech.soc.soar.shared.domain.auth.model.TwoFactorVerificationResult
import tech.soc.soar.shared.domain.auth.repository.AuthRepository

class AuthRepositoryImpl(
    private val authApi: AuthApi
) : AuthRepository {

    override suspend fun login(
        username: String,
        password: String
    ): AppResult<AuthSession> {
        return when (
            val result = authApi.login(
                LoginRequestDto(
                    username = username,
                    password = password
                )
            )
        ) {
            is AppResult.Error -> result
            is AppResult.Success -> AppResult.Success(result.data.toDomain())
        }
    }

    override suspend fun refreshToken(
        refreshToken: String
    ): AppResult<RefreshResult> {
        return when (
            val result = authApi.refreshToken(
                RefreshTokenRequestDto(
                    refreshToken = refreshToken
                )
            )
        ) {
            is AppResult.Error -> result
            is AppResult.Success -> AppResult.Success(result.data.toDomain())
        }
    }

    override suspend fun logout(
        refreshToken: String
    ): AppResult<OperationStatus> {
        return when (
            val result = authApi.logout(
                LogoutRequestDto(
                    refreshToken = refreshToken
                )
            )
        ) {
            is AppResult.Error -> result
            is AppResult.Success -> AppResult.Success(result.data.toDomain())
        }
    }

    override suspend fun whoami(): AppResult<CurrentUser> {
        return when (val result = authApi.whoami()) {
            is AppResult.Error -> result
            is AppResult.Success -> AppResult.Success(result.data.toDomain())
        }
    }

    override suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): AppResult<OperationStatus> {
        return when (
            val result = authApi.changePassword(
                ChangePasswordRequestDto(
                    oldPassword = oldPassword,
                    newPassword = newPassword
                )
            )
        ) {
            is AppResult.Error -> result
            is AppResult.Success -> AppResult.Success(result.data.toDomain())
        }
    }

    override suspend fun verifyTwoFactorCode(
        code: String
    ): AppResult<TwoFactorVerificationResult> {
        return when (
            val result = authApi.verifyTwoFactorCode(
                VerifyTwoFactorCodeRequestDto(
                    code = code
                )
            )
        ) {
            is AppResult.Error -> result
            is AppResult.Success -> AppResult.Success(result.data.toDomain())
        }
    }
}