package tech.soc.soar.shared.data.response.remote

import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.data.response.dto.CreateResponseResultDto

interface ResponseApi {

    suspend fun createBlockIpResponse(
        alertId: Long,
        ip: String,
        fieldName: String,
        message: String
    ): AppResult<CreateResponseResultDto>
}