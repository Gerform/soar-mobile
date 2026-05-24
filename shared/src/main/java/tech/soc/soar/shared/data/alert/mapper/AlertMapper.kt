package tech.soc.soar.shared.data.alert.mapper

import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import tech.soc.soar.shared.data.alert.dto.AlertDetailsDto
import tech.soc.soar.shared.data.alert.dto.AlertDto
import tech.soc.soar.shared.data.alert.local.AlertDetailsEntity
import tech.soc.soar.shared.data.alert.local.AlertEntity
import tech.soc.soar.shared.data.alert.local.AlertViewEntity
import tech.soc.soar.shared.data.alert.local.AlertWithViewEntity
import tech.soc.soar.shared.domain.alert.model.AlertDetails
import tech.soc.soar.shared.domain.alert.model.AlertItem
import tech.soc.soar.shared.domain.response.model.ResponseTarget
import tech.soc.soar.shared.domain.response.model.ResponseTargetType

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

fun AlertDetailsDto.toEntity(): AlertDetailsEntity {
    return AlertDetailsEntity(
        id = id,
        date = date,
        status = status,
        spaceName = spaceName,
        rawBody = rawBody,
        updatedAt = System.currentTimeMillis()
    )
}

fun AlertDetailsDto.toDomain(
    fromCache: Boolean = false
): AlertDetails {
    return AlertDetails(
        id = id,
        date = date,
        status = status,
        spaceName = spaceName,
        rawBody = rawBody.ifBlank {
            "Raw alert body is empty"
        },
        responseTargets = extractResponseTargets(),
        fromCache = fromCache
    )
}

fun AlertDetailsEntity.toDomain(): AlertDetails {
    return AlertDetails(
        id = id,
        date = date,
        status = status,
        spaceName = spaceName,
        rawBody = rawBody,
        fromCache = true
    )
}

private val ipv4Regex = Regex(
    pattern = """\b(?:(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)\.){3}(?:25[0-5]|2[0-4]\d|1\d\d|[1-9]?\d)\b"""
)

private fun AlertDetailsDto.extractResponseTargets(): List<ResponseTarget> {
    val alertsArray = alertBody
        ?.get("alerts")
        ?.jsonArrayOrNull()
        ?: return emptyList()

    val targets = mutableListOf<ResponseTarget>()

    alertsArray.forEach { element ->
        val alertObject = element.jsonObjectOrNull() ?: return@forEach

        alertObject.forEach { entry ->
            val fieldName = entry.key
            val value = entry.value.jsonPrimitiveOrNull()?.contentOrNull ?: return@forEach

            if (ipv4Regex.matches(value)) {
                targets.add(
                    ResponseTarget(
                        fieldName = fieldName,
                        value = value,
                        type = ResponseTargetType.IP
                    )
                )
            }
        }
    }

    return targets.distinctBy {
        "${it.type}:${it.fieldName}:${it.value}"
    }
}

private fun JsonElement.jsonObjectOrNull(): JsonObject? {
    return this as? JsonObject
}

private fun JsonElement.jsonArrayOrNull(): JsonArray? {
    return this as? JsonArray
}

private fun JsonElement.jsonPrimitiveOrNull(): JsonPrimitive? {
    return this as? JsonPrimitive
}