package tech.soc.soar.shared.domain.alert.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.alert.model.AlertsPage
import tech.soc.soar.shared.domain.alert.repository.AlertRepository
import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase

class GetAlertsPageUseCase(
    private val alertRepository: AlertRepository,
    private val checkSessionUseCase: CheckSessionUseCase
) {

    suspend operator fun invoke(
        spaceName: String,
        page: Int,
        pageSize: Int = 50
    ): AppResult<AlertsPage> {
        val sessionState = checkSessionUseCase()

        val userId = when (sessionState) {
            is SessionState.Authenticated -> {
                sessionState.session.user?.uid
                    ?: return AppResult.Error(
                        AppError.Unauthorized("Not authenticated")
                    )
            }

            is SessionState.RequiresTwoFactor,
            SessionState.Loading,
            SessionState.Unauthenticated -> {
                return AppResult.Error(
                    AppError.Unauthorized("Not authenticated")
                )
            }
        }

        return alertRepository.getAlertsPage(
            spaceName = spaceName,
            userId = userId,
            page = page,
            pageSize = pageSize
        )
    }
}