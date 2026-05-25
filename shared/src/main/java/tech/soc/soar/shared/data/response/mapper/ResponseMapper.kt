package tech.soc.soar.shared.data.response.mapper

import tech.soc.soar.shared.data.response.dto.CreateResponseResultDto
import tech.soc.soar.shared.data.response.dto.ResponseRequestDto
import tech.soc.soar.shared.data.response.dto.ResponseRequestsPageDto
import tech.soc.soar.shared.data.response.dto.SuccessfulActionDto
import tech.soc.soar.shared.data.response.dto.SuccessfulActionsPageDto
import tech.soc.soar.shared.data.response.local.ResponseRequestEntity
import tech.soc.soar.shared.data.response.local.SuccessfulActionEntity
import tech.soc.soar.shared.domain.response.model.CreateResponseResult
import tech.soc.soar.shared.domain.response.model.ResponseRequest
import tech.soc.soar.shared.domain.response.model.ResponseRequestsPage
import tech.soc.soar.shared.domain.response.model.SuccessfulAction
import tech.soc.soar.shared.domain.response.model.SuccessfulActionsPage

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

fun SuccessfulActionDto.toEntity(): SuccessfulActionEntity {
    return SuccessfulActionEntity(
        id = id,
        alertId = alertId,
        responseRequestId = responseRequestId,
        approvedUser = approvedUser,
        spaceName = spaceName,
        actionName = actionName,
        targetValue = targetValue,
        createdAt = createdAt,
        cachedAt = System.currentTimeMillis()
    )
}

fun SuccessfulActionDto.toDomain(): SuccessfulAction {
    return SuccessfulAction(
        id = id,
        alertId = alertId,
        responseRequestId = responseRequestId,
        approvedUser = approvedUser,
        spaceName = spaceName,
        actionName = actionName,
        targetValue = targetValue,
        createdAt = createdAt
    )
}

fun SuccessfulActionEntity.toDomain(): SuccessfulAction {
    return SuccessfulAction(
        id = id,
        alertId = alertId,
        responseRequestId = responseRequestId,
        approvedUser = approvedUser,
        spaceName = spaceName,
        actionName = actionName,
        targetValue = targetValue,
        createdAt = createdAt
    )
}

fun SuccessfulActionsPageDto.toDomain(
    fromCache: Boolean = false
): SuccessfulActionsPage {
    return SuccessfulActionsPage(
        total = total,
        skip = skip,
        limit = limit,
        successfulActions = successfulActions.map { it.toDomain() },
        fromCache = fromCache
    )
}