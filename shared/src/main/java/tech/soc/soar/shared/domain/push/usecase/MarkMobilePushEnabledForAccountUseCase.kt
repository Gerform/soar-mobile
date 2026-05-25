package tech.soc.soar.shared.domain.push.usecase

import tech.soc.soar.shared.domain.push.repository.MobilePushRepository

class MarkMobilePushEnabledForAccountUseCase(
    private val mobilePushRepository: MobilePushRepository
) {

    suspend operator fun invoke(
        accountUid: String,
        deviceId: String
    ) {
        mobilePushRepository.markPushEnabledForAccountDevice(
            accountUid = accountUid,
            deviceId = deviceId
        )
    }
}