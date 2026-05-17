package tech.soc.soar.shared.data.alert.repository

import android.content.Context
import tech.soc.soar.shared.data.alert.remote.AlertApi
import tech.soc.soar.shared.data.database.DatabaseFactory
import tech.soc.soar.shared.domain.alert.repository.AlertRepository

object AlertRepositoryFactory {

    fun create(
        context: Context,
        alertApi: AlertApi
    ): AlertRepository {
        val database = DatabaseFactory.create(context)

        return AlertRepositoryImpl(
            alertApi = alertApi,
            alertDao = database.alertDao()
        )
    }
}