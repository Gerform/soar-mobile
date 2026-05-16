package tech.soc.soar.shared.core.network

import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.http.HttpStatusCode
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import tech.soc.soar.shared.core.error.AppError

object ApiErrorMapper {

    private val json = Json {
        ignoreUnknownKeys = true
    }

    suspend fun map(response: HttpResponse): AppError {
        val message = extractServerMessage(response)

        return when (response.status) {
            HttpStatusCode.Unauthorized -> {
                AppError.Unauthorized(
                    message = message ?: "Unauthorized"
                )
            }

            HttpStatusCode.UnprocessableEntity -> {
                AppError.Validation(
                    message = message ?: "Validation error"
                )
            }

            else -> {
                AppError.Api(
                    message = message ?: defaultMessageForStatus(response.status.value)
                )
            }
        }
    }

    private suspend fun extractServerMessage(response: HttpResponse): String? {
        val body = runCatching {
            response.bodyAsText()
        }.getOrNull()

        if (body.isNullOrBlank()) {
            return null
        }

        return runCatching {
            val root = json.parseToJsonElement(body).jsonObject
            val detail = root["detail"]

            when (detail) {
                is JsonPrimitive -> {
                    detail.contentOrNull
                }

                is JsonArray -> {
                    extractValidationMessage(detail)
                }

                is JsonObject -> {
                    extractObjectMessage(detail)
                }

                else -> null
            }
        }.getOrNull()
    }

    private fun extractValidationMessage(detail: JsonArray): String? {
        val firstError = detail.firstOrNull() as? JsonObject
            ?: return null

        val msg = (firstError["msg"] as? JsonPrimitive)
            ?.contentOrNull

        return msg
    }

    private fun extractObjectMessage(detail: JsonObject): String? {
        return (detail["msg"] as? JsonPrimitive)
            ?.contentOrNull
    }

    private fun defaultMessageForStatus(statusCode: Int): String {
        return when (statusCode) {
            400 -> "Bad request"
            403 -> "Forbidden"
            404 -> "Resource not found"
            429 -> "Too many requests. Try again later"
            500 -> "Internal server error"
            502 -> "Bad gateway"
            503 -> "Server unavailable"
            504 -> "Gateway timeout"
            else -> "Request failed"
        }
    }
}