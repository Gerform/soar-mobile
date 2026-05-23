package tech.soc.soar.shared.domain.alert.repository

import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.alert.model.AlertDetails
import tech.soc.soar.shared.domain.alert.model.AlertsPage

interface AlertRepository {

    suspend fun getAlertsPage(
        spaceName: String,
        userId: Int,
        page: Int,
        pageSize: Int
    ): AppResult<AlertsPage>

    suspend fun getAlertDetails(
        alertId: Long,
        spaceName: String,
        userId: Int
    ): AppResult<AlertDetails>

    suspend fun markAlertViewed(
        alertId: Long,
        userId: Int
    )

    suspend fun updateAlertStatus(
        alertId: Long,
        status: String
    ): AppResult<String>
}