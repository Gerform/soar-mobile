package tech.soc.soar.shared.domain.alert.usecase

import tech.soc.soar.shared.domain.alert.repository.AlertRepository

class UpdateCachedAlertStatusUseCase(
    private val alertRepository: AlertRepository
) {

    suspend operator fun invoke(
        alertId: Long,
        status: String
    ) {
        alertRepository.updateCachedAlertStatus(
            alertId = alertId,
            status = status
        )
    }
}