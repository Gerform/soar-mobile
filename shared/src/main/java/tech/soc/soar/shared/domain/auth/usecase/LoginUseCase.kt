package tech.soc.soar.shared.domain.auth.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.model.LoginResult
import tech.soc.soar.shared.domain.auth.repository.AuthRepository
import tech.soc.soar.shared.domain.auth.repository.SessionRepository

class LoginUseCase(
    private val authRepository: AuthRepository,
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(
        username: String,
        password: String
    ): AppResult<LoginResult> {
        val normalizedUsername = username.trim()

        if (normalizedUsername.isBlank()) {
            return AppResult.Error(
                AppError.Validation("Username is required")
            )
        }

        if (password.isBlank()) {
            return AppResult.Error(
                AppError.Validation("Password is required")
            )
        }

        return when (val loginResult = authRepository.login(normalizedUsername, password)) {
            is AppResult.Error -> loginResult

            is AppResult.Success -> {
                val session = loginResult.data

                sessionRepository.saveSession(session)

                if (session.needTwoFactor) {
                    AppResult.Success(
                        LoginResult.TwoFactorRequired(session)
                    )
                } else {
                    when (val userResult = authRepository.whoami()) {
                        is AppResult.Error -> {
                            AppResult.Success(
                                LoginResult.Success(session)
                            )
                        }

                        is AppResult.Success -> {
                            val user = userResult.data
                            val updatedSession = session.copy(user = user)

                            sessionRepository.saveSession(updatedSession)
                            sessionRepository.saveCurrentUser(user)

                            AppResult.Success(
                                LoginResult.Success(updatedSession)
                            )
                        }
                    }
                }
            }
        }
    }
}