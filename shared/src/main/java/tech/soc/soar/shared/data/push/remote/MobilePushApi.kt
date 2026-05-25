package tech.soc.soar.shared.data.push.remote

import tech.soc.soar.shared.core.result.AppResult

interface MobilePushApi {

    suspend fun subscribe(
        token: String,
        deviceId: String,
        deviceName: String
    ): AppResult<String>
}