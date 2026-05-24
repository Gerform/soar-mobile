package tech.soc.soar.shared.data.response.repository

import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.data.response.mapper.toDomain
import tech.soc.soar.shared.data.response.remote.ResponseApi
import tech.soc.soar.shared.domain.response.model.CreateResponseResult
import tech.soc.soar.shared.domain.response.repository.ResponseRepository

class ResponseRepositoryImpl(
    private val responseApi: ResponseApi
) : ResponseRepository {

    override suspend fun createBlockIpResponse(
        alertId: Long,
        ip: String,
        fieldName: String,
        message: String
    ): AppResult<CreateResponseResult> {
        return when (
            val result = responseApi.createBlockIpResponse(
                alertId = alertId,
                ip = ip,
                fieldName = fieldName,
                message = message
            )
        ) {
            is AppResult.Success -> {
                AppResult.Success(
                    result.data.toDomain()
                )
            }

            is AppResult.Error -> {
                result
            }
        }
    }
}