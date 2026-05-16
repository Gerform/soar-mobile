package tech.soc.soar.shared.core.network

import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import tech.soc.soar.shared.core.error.AppError

object ApiErrorMapper {

    fun map(response: HttpResponse): AppError {
        return when (response.status) {
            HttpStatusCode.BadRequest -> {
                AppError.BadRequest("Bad request")
            }

            HttpStatusCode.Unauthorized -> {
                AppError.Unauthorized("Unauthorized")
            }

            HttpStatusCode.Forbidden -> {
                AppError.Forbidden("Forbidden")
            }

            HttpStatusCode.NotFound -> {
                AppError.NotFound("Resource not found")
            }

            HttpStatusCode.InternalServerError -> {
                AppError.Server("Internal server error")
            }

            HttpStatusCode.BadGateway -> {
                AppError.Server("Bad gateway")
            }

            HttpStatusCode.ServiceUnavailable -> {
                AppError.Server("Service unavailable")
            }

            else -> {
                AppError.Unknown("Unexpected response: ${response.status.value}")
            }
        }
    }
}