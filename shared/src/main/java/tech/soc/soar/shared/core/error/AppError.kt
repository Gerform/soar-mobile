package tech.soc.soar.shared.core.error

sealed interface AppError {

    data class Validation(
        val message: String
    ) : AppError

    data class BadRequest(
        val message: String = "Bad request"
    ) : AppError

    data class Unauthorized(
        val message: String = "Invalid token or session expired"
    ) : AppError

    data class Forbidden(
        val message: String = "Not authenticated or two-factor authentication required"
    ) : AppError

    data class NotFound(
        val message: String = "Resource not found"
    ) : AppError

    data class Network(
        val message: String = "Network error"
    ) : AppError

    data class Server(
        val message: String = "Server error"
    ) : AppError

    data class Unknown(
        val message: String = "Unknown error"
    ) : AppError
}