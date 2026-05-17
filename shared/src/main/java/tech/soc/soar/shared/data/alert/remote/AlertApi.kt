package tech.soc.soar.shared.data.alert.remote

import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.data.alert.dto.AlertDto

interface AlertApi {

    suspend fun getAlerts(
        spaceName: String,
        skip: Int,
        limit: Int
    ): AppResult<List<AlertDto>>
}