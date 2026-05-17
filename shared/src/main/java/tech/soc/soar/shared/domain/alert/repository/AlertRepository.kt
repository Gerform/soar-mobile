package tech.soc.soar.shared.domain.alert.repository

import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.alert.model.AlertsPage

interface AlertRepository {

    suspend fun getAlertsPage(
        spaceName: String,
        userId: Int,
        page: Int,
        pageSize: Int
    ): AppResult<AlertsPage>
}