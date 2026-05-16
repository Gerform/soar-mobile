package tech.soc.soar.shared.domain.auth.repository

import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.model.AuthSession
import tech.soc.soar.shared.domain.auth.model.CurrentUser
import tech.soc.soar.shared.domain.auth.model.OperationStatus
import tech.soc.soar.shared.domain.auth.model.RefreshResult
import tech.soc.soar.shared.domain.auth.model.TwoFactorVerificationResult

interface AuthRepository {

    suspend fun login(
        username: String,
        password: String
    ): AppResult<AuthSession>

    suspend fun refreshToken(
        refreshToken: String
    ): AppResult<RefreshResult>

    suspend fun logout(
        refreshToken: String
    ): AppResult<OperationStatus>

    suspend fun whoami(): AppResult<CurrentUser>

    suspend fun changePassword(
        oldPassword: String,
        newPassword: String
    ): AppResult<OperationStatus>

    suspend fun verifyTwoFactorCode(
        code: String
    ): AppResult<TwoFactorVerificationResult>
}