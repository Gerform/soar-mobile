package tech.soc.soar.shared.domain.response.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.usecase.RefreshSessionUseCase
import tech.soc.soar.shared.domain.response.model.SuccessfulActionsPage
import tech.soc.soar.shared.domain.response.repository.ResponseRepository

class GetSuccessfulActionsUseCase(
    private val responseRepository: ResponseRepository,
    private val refreshSessionUseCase: RefreshSessionUseCase
) {

    suspend operator fun invoke(
        alertId: Long,
        page: Int = 0,
        pageSize: Int = 50
    ): AppResult<SuccessfulActionsPage> {
        val firstResult = responseRepository.getSuccessfulActionsByAlertId(
            alertId = alertId,
            page = page,
            pageSize = pageSize
        )

        if (firstResult is AppResult.Success) {
            return firstResult
        }

        val firstError = firstResult as AppResult.Error

        if (firstError.error !is AppError.Unauthorized) {
            return firstError
        }

        val refreshResult = refreshSessionUseCase()

        if (refreshResult is AppResult.Error) {
            return AppResult.Error(
                AppError.Unauthorized("Session expired")
            )
        }

        return responseRepository.getSuccessfulActionsByAlertId(
            alertId = alertId,
            page = page,
            pageSize = pageSize
        )
    }
}