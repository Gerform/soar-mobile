package tech.soc.soar.shared.data.response.repository

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.data.response.local.ResponseDao
import tech.soc.soar.shared.data.response.mapper.toDomain
import tech.soc.soar.shared.data.response.mapper.toEntity
import tech.soc.soar.shared.data.response.remote.ResponseApi
import tech.soc.soar.shared.domain.response.model.CreateResponseResult
import tech.soc.soar.shared.domain.response.model.ResponseRequestsPage
import tech.soc.soar.shared.domain.response.repository.ResponseRepository
import java.time.Instant

class ResponseRepositoryImpl(
    private val responseApi: ResponseApi,
    private val responseDao: ResponseDao
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
                AppResult.Success(result.data.toDomain())
            }

            is AppResult.Error -> {
                result
            }
        }
    }

    override suspend fun getResponseRequestsByAlertId(
        alertId: Long,
        page: Int,
        pageSize: Int
    ): AppResult<ResponseRequestsPage> {
        val skip = page * pageSize

        return when (
            val result = responseApi.getResponseRequestsByAlertId(
                alertId = alertId,
                skip = skip,
                limit = pageSize
            )
        ) {
            is AppResult.Success -> {
                val pageDto = result.data

                responseDao.upsertResponseRequests(
                    pageDto.responseRequests.map { it.toEntity() }
                )

                AppResult.Success(
                    pageDto.toDomain(fromCache = false)
                )
            }

            is AppResult.Error -> {
                if (
                    result.error is AppError.Unauthorized ||
                    result.error is AppError.Forbidden
                ) {
                    return result
                }

                val cachedRequests = responseDao.getResponseRequestsByAlertId(
                    alertId = alertId,
                    skip = skip,
                    limit = pageSize
                )

                if (cachedRequests.isNotEmpty()) {
                    val total = responseDao.countResponseRequestsByAlertId(
                        alertId = alertId
                    )

                    AppResult.Success(
                        ResponseRequestsPage(
                            total = total,
                            skip = skip,
                            limit = pageSize,
                            responseRequests = cachedRequests.map { it.toDomain() },
                            fromCache = true
                        )
                    )
                } else {
                    AppResult.Error(result.error)
                }
            }
        }
    }

    override suspend fun decideResponseRequest(
        responseRequestId: Long,
        decision: String
    ): AppResult<CreateResponseResult> {
        return when (
            val result = responseApi.decideResponseRequest(
                responseRequestId = responseRequestId,
                decision = decision
            )
        ) {
            is AppResult.Success -> {
                val newStatus = decision
                val now = Instant.now().toString()

                responseDao.updateResponseRequestStatus(
                    responseRequestId = responseRequestId,
                    status = newStatus,
                    updatedAt = now,
                    cachedAt = System.currentTimeMillis()
                )

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