package tech.soc.soar.shared.domain.response.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase
import tech.soc.soar.shared.domain.auth.usecase.RefreshSessionUseCase
import tech.soc.soar.shared.domain.response.model.CreateResponseResult
import tech.soc.soar.shared.domain.response.model.ResponseActionType
import tech.soc.soar.shared.domain.response.model.ResponsePermissions
import tech.soc.soar.shared.domain.response.repository.ResponseRepository

class CreateResponseActionUseCase(
    private val responseRepository: ResponseRepository,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val refreshSessionUseCase: RefreshSessionUseCase
) {

    suspend operator fun invoke(
        actionType: String,
        alertId: Long,
        targetValue: String,
        fieldName: String?,
        message: String
    ): AppResult<CreateResponseResult> {
        val normalizedActionType = actionType.lowercase().trim()
        val normalizedTargetValue = targetValue.trim()
        val normalizedMessage = message.trim()

        if (normalizedMessage.isBlank()) {
            return AppResult.Error(
                AppError.Validation("Message is required")
            )
        }

        val sessionState = checkSessionUseCase()

        if (sessionState !is SessionState.Authenticated) {
            return AppResult.Error(
                AppError.Unauthorized("Not authenticated")
            )
        }

        if (!ResponsePermissions.canCreateResponse(sessionState.session.roles)) {
            return AppResult.Error(
                AppError.Forbidden("Responder privileges required")
            )
        }

        val firstResult = sendRequest(
            actionType = normalizedActionType,
            alertId = alertId,
            targetValue = normalizedTargetValue,
            fieldName = fieldName,
            message = normalizedMessage
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

        return sendRequest(
            actionType = normalizedActionType,
            alertId = alertId,
            targetValue = normalizedTargetValue,
            fieldName = fieldName,
            message = normalizedMessage
        )
    }

    private suspend fun sendRequest(
        actionType: String,
        alertId: Long,
        targetValue: String,
        fieldName: String?,
        message: String
    ): AppResult<CreateResponseResult> {
        return when (actionType) {
            ResponseActionType.BLOCK_IP -> {
                if (fieldName.isNullOrBlank()) {
                    return AppResult.Error(
                        AppError.Validation("Field name is required")
                    )
                }

                responseRepository.createBlockIpResponse(
                    alertId = alertId,
                    ip = targetValue,
                    fieldName = fieldName,
                    message = message
                )
            }

            ResponseActionType.UNBLOCK_IP -> {
                responseRepository.createUnblockIpResponse(
                    alertId = alertId,
                    ip = targetValue,
                    message = message
                )
            }

            else -> {
                AppResult.Error(
                    AppError.Validation("Unsupported response action")
                )
            }
        }
    }
}