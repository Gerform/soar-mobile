package tech.soc.soar.shared.data.response.mapper

import tech.soc.soar.shared.data.response.dto.CreateResponseResultDto
import tech.soc.soar.shared.data.response.dto.ResponseRequestDto
import tech.soc.soar.shared.data.response.dto.ResponseRequestsPageDto
import tech.soc.soar.shared.data.response.local.ResponseRequestEntity
import tech.soc.soar.shared.domain.response.model.CreateResponseResult
import tech.soc.soar.shared.domain.response.model.ResponseRequest
import tech.soc.soar.shared.domain.response.model.ResponseRequestsPage

fun CreateResponseResultDto.toDomain(): CreateResponseResult {
    return CreateResponseResult(
        status = status
    )
}

fun ResponseRequestDto.toEntity(): ResponseRequestEntity {
    return ResponseRequestEntity(
        id = id,
        alertId = alertId,
        createdByUser = createdByUser,
        spaceName = spaceName,
        actionName = actionName,
        status = status,
        targetValue = targetValue,
        requestBodyJson = requestBody?.toString().orEmpty(),
        message = message,
        createdAt = createdAt,
        updatedAt = updatedAt,
        cachedAt = System.currentTimeMillis()
    )
}

fun ResponseRequestDto.toDomain(): ResponseRequest {
    return ResponseRequest(
        id = id,
        alertId = alertId,
        createdByUser = createdByUser,
        spaceName = spaceName,
        actionName = actionName,
        status = status,
        targetValue = targetValue,
        message = message,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ResponseRequestEntity.toDomain(): ResponseRequest {
    return ResponseRequest(
        id = id,
        alertId = alertId,
        createdByUser = createdByUser,
        spaceName = spaceName,
        actionName = actionName,
        status = status,
        targetValue = targetValue,
        message = message,
        createdAt = createdAt,
        updatedAt = updatedAt
    )
}

fun ResponseRequestsPageDto.toDomain(
    fromCache: Boolean = false
): ResponseRequestsPage {
    return ResponseRequestsPage(
        total = total,
        skip = skip,
        limit = limit,
        responseRequests = responseRequests.map { it.toDomain() },
        fromCache = fromCache
    )
}