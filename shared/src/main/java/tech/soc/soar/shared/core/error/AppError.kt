package tech.soc.soar.shared.core.error

sealed interface AppError {

    val message: String

    data class Api(
        override val message: String
    ) : AppError

    data class Validation(
        override val message: String
    ) : AppError

    data class Unauthorized(
        override val message: String
    ) : AppError

    data class Forbidden(
        override val message: String
    ) : AppError

    data class Network(
        override val message: String = "Failed to connect to server"
    ) : AppError

    data class Unknown(
        override val message: String = "Unknown error"
    ) : AppError
}