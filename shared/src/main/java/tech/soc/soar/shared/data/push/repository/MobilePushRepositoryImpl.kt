package tech.soc.soar.shared.data.push.repository

import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.data.push.local.MobilePushPermissionDao
import tech.soc.soar.shared.data.push.local.MobilePushPermissionEntity
import tech.soc.soar.shared.data.push.remote.MobilePushApi
import tech.soc.soar.shared.domain.push.repository.MobilePushRepository

class MobilePushRepositoryImpl(
    private val mobilePushApi: MobilePushApi,
    private val mobilePushPermissionDao: MobilePushPermissionDao
) : MobilePushRepository {

    override suspend fun subscribe(
        token: String,
        deviceId: String,
        deviceName: String
    ): AppResult<String> {
        return mobilePushApi.subscribe(
            token = token,
            deviceId = deviceId,
            deviceName = deviceName
        )
    }

    override suspend fun isPushEnabledForAccountDevice(
        accountUid: String,
        deviceId: String
    ): Boolean {
        return mobilePushPermissionDao.isPushEnabledForAccountDevice(
            accountUid = accountUid,
            deviceId = deviceId
        ) == true
    }

    override suspend fun markPushEnabledForAccountDevice(
        accountUid: String,
        deviceId: String
    ) {
        mobilePushPermissionDao.upsertPermission(
            MobilePushPermissionEntity(
                accountUid = accountUid,
                deviceId = deviceId,
                enabled = true,
                updatedAt = System.currentTimeMillis()
            )
        )
    }
}