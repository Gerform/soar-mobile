package tech.soc.soar.shared.data.alert.mapper

import tech.soc.soar.shared.data.alert.dto.AlertDto
import tech.soc.soar.shared.data.alert.local.AlertEntity
import tech.soc.soar.shared.data.alert.local.AlertViewEntity
import tech.soc.soar.shared.data.alert.local.AlertWithViewEntity
import tech.soc.soar.shared.domain.alert.model.AlertItem

fun AlertDto.toEntity(): AlertEntity {
    return AlertEntity(
        id = id,
        date = date,
        status = status,
        spaceName = spaceName,
        reason = reason
    )
}

fun AlertDto.toViewEntity(userId: Int): AlertViewEntity {
    return AlertViewEntity(
        alertId = id,
        userId = userId,
        isViewed = isViewed,
        updatedAt = System.currentTimeMillis()
    )
}

fun AlertDto.toDomain(): AlertItem {
    return AlertItem(
        id = id,
        date = date,
        status = status,
        spaceName = spaceName,
        isViewed = isViewed,
        reason = reason
    )
}

fun AlertWithViewEntity.toDomain(): AlertItem {
    return AlertItem(
        id = id,
        date = date,
        status = status,
        spaceName = spaceName,
        isViewed = isViewed ?: false,
        reason = reason
    )
}