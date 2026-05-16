package tech.soc.soar.shared.domain.auth.usecase

import tech.soc.soar.shared.domain.auth.model.SessionState
import tech.soc.soar.shared.domain.auth.repository.SessionRepository

class CheckSessionUseCase(
    private val sessionRepository: SessionRepository
) {

    suspend operator fun invoke(): SessionState {
        return sessionRepository.getSessionState()
    }
}