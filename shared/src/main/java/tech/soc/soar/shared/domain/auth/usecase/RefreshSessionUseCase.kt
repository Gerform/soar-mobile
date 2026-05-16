package tech.soc.soar.shared.domain.auth.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.model.RefreshResult
import tech.soc.soar.shared.domain.auth.repository.AuthRepository
import tech.soc.soar.shared.domain.auth.repository.SessionRepository

class RefreshSessionUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(): AppResult<RefreshResult> {
        val refreshToken = sessionRepository.getRefreshToken()
            ?: return AppResult.Error(
                AppError.Unauthorized("Refresh token not found")
            )

        return when (val result = authRepository.refreshToken(refreshToken)) {
            is AppResult.Error -> {
                sessionRepository.clearSession()
                result
            }

            is AppResult.Success -> {
                val refreshResult = result.data

                sessionRepository.updateAccessToken(
                    accessToken = refreshResult.accessToken,
                    tokenType = refreshResult.tokenType
                )

                AppResult.Success(refreshResult)
            }
        }
    }
}