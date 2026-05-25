package tech.soc.soar.shared.data.push.repository

import android.content.Context
import tech.soc.soar.shared.data.database.DatabaseFactory
import tech.soc.soar.shared.data.push.remote.MobilePushApi
import tech.soc.soar.shared.domain.push.repository.MobilePushRepository

object MobilePushRepositoryFactory {

    fun create(
        context: Context,
        mobilePushApi: MobilePushApi
    ): MobilePushRepository {
        val database = DatabaseFactory.create(context)

        return MobilePushRepositoryImpl(
            mobilePushApi = mobilePushApi,
            mobilePushPermissionDao = database.mobilePushPermissionDao()
        )
    }
}