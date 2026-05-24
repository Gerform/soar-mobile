package tech.soc.soar.shared.data.response.repository

import android.content.Context
import tech.soc.soar.shared.data.database.DatabaseFactory
import tech.soc.soar.shared.data.response.remote.ResponseApi
import tech.soc.soar.shared.domain.response.repository.ResponseRepository

object ResponseRepositoryFactory {

    fun create(
        context: Context,
        responseApi: ResponseApi
    ): ResponseRepository {
        val database = DatabaseFactory.create(context)

        return ResponseRepositoryImpl(
            responseApi = responseApi,
            responseDao = database.responseDao()
        )
    }
}