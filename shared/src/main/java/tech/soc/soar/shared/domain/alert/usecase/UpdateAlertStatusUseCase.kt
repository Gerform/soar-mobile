package tech.soc.soar.shared.domain.alert.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.alert.model.AlertStatus
import tech.soc.soar.shared.domain.alert.repository.AlertRepository
import tech.soc.soar.shared.domain.auth.usecase.RefreshSessionUseCase

class UpdateAlertStatusUseCase(
    private val alertRepository: AlertRepository,
    private val refreshSessionUseCase: RefreshSessionUseCase
) {

    suspend operator fun invoke(
        alertId: Long,
        currentStatus: String,
        newStatus: String
    ): AppResult<String> {
        if (currentStatus.lowercase().trim() != AlertStatus.NEW) {
            return AppResult.Error(
                AppError.Api("Only new alerts can be updated")
            )
        }

        val normalizedNewStatus = newStatus.lowercase().trim()

        if (
            normalizedNewStatus != AlertStatus.FALSE_POSITIVE &&
            normalizedNewStatus != AlertStatus.CLOSED
        ) {
            return AppResult.Error(
                AppError.Api("Invalid target status")
            )
        }

        val firstResult = alertRepository.updateAlertStatus(
            alertId = alertId,
            status = normalizedNewStatus
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

        return alertRepository.updateAlertStatus(
            alertId = alertId,
            status = normalizedNewStatus
        )
    }
}