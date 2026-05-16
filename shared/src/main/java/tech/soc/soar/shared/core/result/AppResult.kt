package tech.soc.soar.shared.core.result

import tech.soc.soar.shared.core.error.AppError

sealed interface AppResult<out T> {

    data class Success<T>(
        val data: T
    ) : AppResult<T>

    data class Error(
        val error: AppError
    ) : AppResult<Nothing>
}