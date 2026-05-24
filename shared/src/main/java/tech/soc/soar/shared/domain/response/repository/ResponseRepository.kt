package tech.soc.soar.shared.domain.response.repository

import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.response.model.CreateResponseResult

interface ResponseRepository {

    suspend fun createBlockIpResponse(
        alertId: Long,
        ip: String,
        fieldName: String,
        message: String
    ): AppResult<CreateResponseResult>
}