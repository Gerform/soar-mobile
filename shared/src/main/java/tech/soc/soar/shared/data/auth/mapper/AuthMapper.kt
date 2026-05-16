package tech.soc.soar.shared.data.auth.mapper

import tech.soc.soar.shared.data.auth.dto.LoginResponseDto
import tech.soc.soar.shared.data.auth.dto.OperationStatusDto
import tech.soc.soar.shared.data.auth.dto.RefreshTokenResponseDto
import tech.soc.soar.shared.data.auth.dto.VerifyTwoFactorCodeResponseDto
import tech.soc.soar.shared.data.auth.dto.WhoamiResponseDto
import tech.soc.soar.shared.domain.auth.model.AuthSession
import tech.soc.soar.shared.domain.auth.model.AuthTokens
import tech.soc.soar.shared.domain.auth.model.CurrentUser
import tech.soc.soar.shared.domain.auth.model.OperationStatus
import tech.soc.soar.shared.domain.auth.model.RefreshResult
import tech.soc.soar.shared.domain.auth.model.TwoFactorVerificationResult

fun LoginResponseDto.toDomain(): AuthSession {
    return AuthSession(
        tokens = AuthTokens(
            accessToken = accessToken,
            refreshToken = refreshToken,
            tokenType = tokenType
        ),
        roles = roles,
        user = null,
        needTwoFactor = needTwoFactor
    )
}

fun RefreshTokenResponseDto.toDomain(): RefreshResult {
    return RefreshResult(
        accessToken = accessToken,
        tokenType = tokenType
    )
}

fun OperationStatusDto.toDomain(): OperationStatus {
    return OperationStatus(
        status = status
    )
}

fun WhoamiResponseDto.toDomain(): CurrentUser {
    return CurrentUser(
        uid = uid,
        username = username,
        mail = mail,
        roles = roles,
        lastConnection = lastConnection,
        twoFactorConfirmed = twoFactorConfirmed
    )
}

fun VerifyTwoFactorCodeResponseDto.toDomain(): TwoFactorVerificationResult {
    return TwoFactorVerificationResult(
        status = status,
        newAccessToken = newAccessToken
    )
}