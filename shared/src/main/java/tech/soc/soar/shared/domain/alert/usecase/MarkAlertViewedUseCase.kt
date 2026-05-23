package tech.soc.soar.shared.domain.alert.usecase

import tech.soc.soar.shared.domain.account.usecase.GetLastUsedAccountUseCase
import tech.soc.soar.shared.domain.alert.repository.AlertRepository
import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase

class MarkAlertViewedUseCase(
    private val alertRepository: AlertRepository,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val getLastUsedAccountUseCase: GetLastUsedAccountUseCase
) {

    suspend operator fun invoke(alertId: Long) {
        val userId = getCurrentUserId() ?: return

        alertRepository.markAlertViewed(
            alertId = alertId,
            userId = userId
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