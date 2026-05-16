package tech.soc.soar.shared.domain.auth.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.model.OperationStatus
import tech.soc.soar.shared.domain.auth.repository.AuthRepository
import tech.soc.soar.shared.domain.auth.repository.SessionRepository

class LogoutUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(): AppResult<OperationStatus> {
        val refreshToken = sessionRepository.getRefreshToken()

        if (refreshToken == null) {
            sessionRepository.clearSession()

            return AppResult.Error(
                AppError.Unauthorized("Refresh token not found")
            )
        }

        val result = authRepository.logout(refreshToken)

        sessionRepository.clearSession()

        return result
    }
}