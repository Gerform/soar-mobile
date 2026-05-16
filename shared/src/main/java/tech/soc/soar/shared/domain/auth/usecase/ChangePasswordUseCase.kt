package tech.soc.soar.shared.domain.auth.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.model.OperationStatus
import tech.soc.soar.shared.domain.auth.repository.AuthRepository

class ChangePasswordUseCase(
    private val authRepository: AuthRepository
) {

    suspend operator fun invoke(
        oldPassword: String,
        newPassword: String
    ): AppResult<OperationStatus> {
        if (oldPassword.isBlank()) {
            return AppResult.Error(
                AppError.Validation("Old password is required")
            )
        }

        if (newPassword.isBlank()) {
            return AppResult.Error(
                AppError.Validation("New password is required")
            )
        }

        if (newPassword.length < 8) {
            return AppResult.Error(
                AppError.Validation("New password must contain at least 8 characters")
            )
        }

        return authRepository.changePassword(
            oldPassword = oldPassword,
            newPassword = newPassword
        )
    }
}