package tech.soc.soar.shared.domain.response.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase
import tech.soc.soar.shared.domain.auth.usecase.RefreshSessionUseCase
import tech.soc.soar.shared.domain.response.model.CreateResponseResult
import tech.soc.soar.shared.domain.response.model.ResponsePermissions
import tech.soc.soar.shared.domain.response.repository.ResponseRepository

class CreateBlockIpResponseUseCase(
    private val responseRepository: ResponseRepository,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val refreshSessionUseCase: RefreshSessionUseCase
) {

    suspend operator fun invoke(
        alertId: Long,
        ip: String,
        fieldName: String,
        message: String
    ): AppResult<CreateResponseResult> {
        val sessionState = checkSessionUseCase()

        if (sessionState !is SessionState.Authenticated) {
            return AppResult.Error(
                AppError.Unauthorized("Not authenticated")
            )
        }

        val roles = sessionState.session.roles

        if (!ResponsePermissions.canCreateResponse(roles)) {
            return AppResult.Error(
                AppError.Forbidden("Responder privileges required")
            )
        }

        if (message.isBlank()) {
            return AppResult.Error(
                AppError.Validation("Message is required")
            )
        }

        val firstResult = responseRepository.createBlockIpResponse(
            alertId = alertId,
            ip = ip,
            fieldName = fieldName,
            message = message.trim()
        )

        if (firstResult is AppResult.Success) {
            return firstResult
        }

        val firstError = firstResult as AppResult.Error

        if (firstError.error !is AppError.Unauthorized) {
            return firstError
        }

        val refreshResult = refreshSessionUseCase()

        if (refreshResult is AppResult.Error) {
            return AppResult.Error(
                AppError.Unauthorized("Session expired")
            )
        }

        return responseRepository.createBlockIpResponse(
            alertId = alertId,
            ip = ip,
            fieldName = fieldName,
            message = message.trim()
        )
    }
}