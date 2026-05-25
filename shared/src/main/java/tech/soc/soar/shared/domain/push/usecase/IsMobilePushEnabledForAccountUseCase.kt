package tech.soc.soar.shared.domain.push.usecase

import tech.soc.soar.shared.domain.push.repository.MobilePushRepository

class IsMobilePushEnabledForAccountUseCase(
    private val mobilePushRepository: MobilePushRepository
) {

    suspend operator fun invoke(
        accountUid: String,
        deviceId: String
    ): Boolean {
        return mobilePushRepository.isPushEnabledForAccountDevice(
            accountUid = accountUid,
            deviceId = deviceId
        )
    }
}