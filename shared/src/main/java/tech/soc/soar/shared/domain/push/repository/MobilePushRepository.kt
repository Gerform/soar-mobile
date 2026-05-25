package tech.soc.soar.shared.domain.push.repository

import tech.soc.soar.shared.core.result.AppResult

interface MobilePushRepository {

    suspend fun subscribe(
        token: String,
        deviceId: String,
        deviceName: String
    ): AppResult<String>

    suspend fun isPushEnabledForAccountDevice(
        accountUid: String,
        deviceId: String
    ): Boolean

    suspend fun markPushEnabledForAccountDevice(
        accountUid: String,
        deviceId: String
    )
}