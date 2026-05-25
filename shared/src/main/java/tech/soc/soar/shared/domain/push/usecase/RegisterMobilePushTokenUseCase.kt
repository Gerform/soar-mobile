package tech.soc.soar.shared.domain.push.usecase

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.domain.auth.usecase.RefreshSessionUseCase
import tech.soc.soar.shared.domain.push.repository.MobilePushRepository

class RegisterMobilePushTokenUseCase(
    private val mobilePushRepository: MobilePushRepository,
    private val refreshSessionUseCase: RefreshSessionUseCase
) {

    suspend operator fun invoke(
        token: String,
        deviceId: String,
        deviceName: String
    ): AppResult<String> {
        val firstResult = mobilePushRepository.subscribe(
            token = token,
            deviceId = deviceId,
            deviceName = deviceName
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

        return mobilePushRepository.subscribe(
            token = token,
            deviceId = deviceId,
            deviceName = deviceName
        )
    }
}