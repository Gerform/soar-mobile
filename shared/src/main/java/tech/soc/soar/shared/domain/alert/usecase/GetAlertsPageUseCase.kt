package tech.soc.soar.shared.domain.alert.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.account.usecase.GetLastUsedAccountUseCase
import tech.soc.soar.shared.domain.alert.model.AlertsPage
import tech.soc.soar.shared.domain.alert.repository.AlertRepository
import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase
import tech.soc.soar.shared.domain.auth.usecase.RefreshSessionUseCase

class GetAlertsPageUseCase(
    private val alertRepository: AlertRepository,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val refreshSessionUseCase: RefreshSessionUseCase,
    private val getLastUsedAccountUseCase: GetLastUsedAccountUseCase
) {

    suspend operator fun invoke(
        spaceName: String,
        page: Int,
        pageSize: Int = 50
    ): AppResult<AlertsPage> {
        val userId = getCurrentUserId()
            ?: return AppResult.Error(
                AppError.Unauthorized("Not authenticated")
            )

        val firstResult = alertRepository.getAlertsPage(
            spaceName = spaceName,
            userId = userId,
            page = page,
            pageSize = pageSize
        )

        when (firstResult) {
            is AppResult.Success -> {
                return firstResult
            }

            is AppResult.Error -> {
                if (!firstResult.error.isUnauthorized()) {
                    return firstResult
                }
            }
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

        return alertRepository.getAlertsPage(
            spaceName = spaceName,
            userId = refreshedUserId,
            page = page,
            pageSize = pageSize
        )
    }

    private suspend fun getCurrentUserId(): Int? {
        val sessionState = checkSessionUseCase()

        if (sessionState !is SessionState.Authenticated) {
            return null
        }

        val sessionUserId = sessionState.session.user?.uid

        if (sessionUserId != null) {
            return sessionUserId
        }

        return getLastUsedAccountUseCase()?.uid
    }

    private fun AppError.isUnauthorized(): Boolean {
        return this is AppError.Unauthorized
    }
}