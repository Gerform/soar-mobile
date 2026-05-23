package tech.soc.soar.shared.domain.alert.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.account.usecase.GetLastUsedAccountUseCase
import tech.soc.soar.shared.domain.alert.model.AlertDetails
import tech.soc.soar.shared.domain.alert.repository.AlertRepository
import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase
import tech.soc.soar.shared.domain.auth.usecase.RefreshSessionUseCase

class GetAlertDetailsUseCase(
    private val alertRepository: AlertRepository,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val refreshSessionUseCase: RefreshSessionUseCase,
    private val getLastUsedAccountUseCase: GetLastUsedAccountUseCase
) {

    suspend operator fun invoke(
        alertId: Long,
        spaceName: String
    ): AppResult<AlertDetails> {
        val userId = getCurrentUserId()
            ?: return AppResult.Error(
                AppError.Unauthorized("Not authenticated")
            )

        val firstResult = alertRepository.getAlertDetails(
            alertId = alertId,
            spaceName = spaceName,
            userId = userId
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

        val refreshedUserId = getCurrentUserId()
            ?: return AppResult.Error(
                AppError.Unauthorized("Not authenticated")
            )

        return alertRepository.getAlertDetails(
            alertId = alertId,
            spaceName = spaceName,
            userId = refreshedUserId
        )
    }

    private suspend fun getCurrentUserId(): Int? {
        val sessionState = checkSessionUseCase()

        if (sessionState !is SessionState.Authenticated) {
            return null
        }

        return sessionState.session.user?.uid
            ?: getLastUsedAccountUseCase()?.uid
    }
}