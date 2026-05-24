package tech.soc.soar.shared.domain.response.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.usecase.CheckSessionUseCase
import tech.soc.soar.shared.domain.auth.usecase.RefreshSessionUseCase
import tech.soc.soar.shared.domain.response.model.CreateResponseResult
import tech.soc.soar.shared.domain.response.model.ResponseDecision
import tech.soc.soar.shared.domain.response.model.ResponsePermissions
import tech.soc.soar.shared.domain.response.repository.ResponseRepository

class DecideResponseRequestUseCase(
    private val responseRepository: ResponseRepository,
    private val checkSessionUseCase: CheckSessionUseCase,
    private val refreshSessionUseCase: RefreshSessionUseCase
) {

    suspend operator fun invoke(
        responseRequestId: Long,
        decision: String
    ): AppResult<CreateResponseResult> {
        val normalizedDecision = decision.lowercase().trim()

        if (
            normalizedDecision != ResponseDecision.APPROVED &&
            normalizedDecision != ResponseDecision.REJECTED
        ) {
            return AppResult.Error(
                AppError.Validation("Invalid decision")
            )
        }

        val sessionState = checkSessionUseCase()

        if (sessionState !is SessionState.Authenticated) {
            return AppResult.Error(
                AppError.Unauthorized("Not authenticated")
            )
        }

        if (!ResponsePermissions.canDecideResponse(sessionState.session.roles)) {
            return AppResult.Error(
                AppError.Forbidden("Responsible privileges required")
            )
        }

        val firstResult = responseRepository.decideResponseRequest(
            responseRequestId = responseRequestId,
            decision = normalizedDecision
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

        return responseRepository.decideResponseRequest(
            responseRequestId = responseRequestId,
            decision = normalizedDecision
        )
    }
}