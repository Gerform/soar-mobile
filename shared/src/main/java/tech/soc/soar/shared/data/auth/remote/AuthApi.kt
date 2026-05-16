package tech.soc.soar.shared.data.auth.remote

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

interface AuthApi {

    suspend fun login(
        request: LoginRequestDto
    ): AppResult<LoginResponseDto>

    suspend fun refreshToken(
        request: RefreshTokenRequestDto
    ): AppResult<RefreshTokenResponseDto>

    suspend fun logout(
        request: LogoutRequestDto
    ): AppResult<OperationStatusDto>

    suspend fun whoami(): AppResult<WhoamiResponseDto>

    suspend fun changePassword(
        request: ChangePasswordRequestDto
    ): AppResult<OperationStatusDto>

    suspend fun verifyTwoFactorCode(
        request: VerifyTwoFactorCodeRequestDto
    ): AppResult<VerifyTwoFactorCodeResponseDto>
}