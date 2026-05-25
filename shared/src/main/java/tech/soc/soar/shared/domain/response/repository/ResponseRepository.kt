package tech.soc.soar.shared.domain.response.repository

import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.response.model.CreateResponseResult
import tech.soc.soar.shared.domain.response.model.ResponseRequestsPage
import tech.soc.soar.shared.domain.response.model.SuccessfulActionsPage

interface ResponseRepository {

    suspend fun createBlockIpResponse(
        alertId: Long,
        ip: String,
        fieldName: String,
        message: String
    ): AppResult<CreateResponseResult>

    suspend fun getResponseRequestsByAlertId(
        alertId: Long,
        page: Int,
        pageSize: Int
    ): AppResult<ResponseRequestsPage>

    suspend fun decideResponseRequest(
        responseRequestId: Long,
        decision: String
    ): AppResult<CreateResponseResult>

    suspend fun getSuccessfulActionsByAlertId(
        alertId: Long,
        page: Int,
        pageSize: Int
    ): AppResult<SuccessfulActionsPage>
}