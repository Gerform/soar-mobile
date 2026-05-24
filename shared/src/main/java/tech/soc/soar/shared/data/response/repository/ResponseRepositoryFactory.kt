package tech.soc.soar.shared.data.response.repository

import tech.soc.soar.shared.data.response.remote.ResponseApi
import tech.soc.soar.shared.domain.response.repository.ResponseRepository

object ResponseRepositoryFactory {

    fun create(
        responseApi: ResponseApi
    ): ResponseRepository {
        return ResponseRepositoryImpl(
            responseApi = responseApi
        )
    }
}