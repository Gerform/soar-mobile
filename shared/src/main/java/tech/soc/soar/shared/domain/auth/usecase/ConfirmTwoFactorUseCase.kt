package tech.soc.soar.shared.domain.auth.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.model.AuthSession
import tech.soc.soar.shared.domain.auth.repository.AuthRepository
import tech.soc.soar.shared.domain.auth.repository.SessionRepository

class ConfirmTwoFactorUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(code: String): AppResult<AuthSession> {
        val normalizedCode = code.trim()

        if (normalizedCode.isBlank()) {
            return AppResult.Error(
                AppError.Validation("Two-factor code is required")
            )
        }

        return when (val verifyResult = authRepository.verifyTwoFactorCode(normalizedCode)) {
            is AppResult.Error -> verifyResult

            is AppResult.Success -> {
                val currentSession = sessionRepository.getSession()
                    ?: return AppResult.Error(
                        AppError.Unauthorized("Session not found")
                    )

                val currentTokenType = currentSession.tokens.tokenType

                sessionRepository.updateAccessToken(
                    accessToken = verifyResult.data.newAccessToken,
                    tokenType = currentTokenType
                )

                when (val userResult = authRepository.whoami()) {
                    is AppResult.Error -> {
                        AppResult.Error(userResult.error)
                    }

                    is AppResult.Success -> {
                        val user = userResult.data

                        val updatedSession = currentSession.copy(
                            tokens = currentSession.tokens.copy(
                                accessToken = verifyResult.data.newAccessToken
                            ),
                            user = user,
                            needTwoFactor = false
                        )

                        sessionRepository.saveSession(updatedSession)
                        sessionRepository.saveCurrentUser(user)

                        AppResult.Success(updatedSession)
                    }
                }
            }
        }
    }
}