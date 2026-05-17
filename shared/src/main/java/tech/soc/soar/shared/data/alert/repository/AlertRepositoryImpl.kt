package tech.soc.soar.shared.data.alert.repository

import tech.soc.soar.shared.core.error.AppError
import tech.soc.soar.shared.core.result.AppResult
import tech.soc.soar.shared.data.alert.local.AlertDao
import tech.soc.soar.shared.data.alert.mapper.toDomain
import tech.soc.soar.shared.data.alert.mapper.toEntity
import tech.soc.soar.shared.data.alert.mapper.toViewEntity
import tech.soc.soar.shared.data.alert.remote.AlertApi
import tech.soc.soar.shared.domain.alert.model.AlertsPage
import tech.soc.soar.shared.domain.alert.repository.AlertRepository

class AlertRepositoryImpl(
    private val alertApi: AlertApi,
    private val alertDao: AlertDao
) : AlertRepository {

    override suspend fun getAlertsPage(
        spaceName: String,
        userId: Int,
        page: Int,
        pageSize: Int
    ): AppResult<AlertsPage> {
        val skip = page * pageSize

        return when (
            val result = alertApi.getAlerts(
                spaceName = spaceName,
                skip = skip,
                limit = pageSize
            )
        ) {
            is AppResult.Success -> {
                val apiAlerts = result.data

                alertDao.upsertAlerts(
                    apiAlerts.map { it.toEntity() }
                )

                alertDao.upsertAlertViews(
                    apiAlerts.map { it.toViewEntity(userId) }
                )

                AppResult.Success(
                    AlertsPage(
                        alerts = apiAlerts.map { it.toDomain() },
                        page = page,
                        pageSize = pageSize,
                        hasNextPage = apiAlerts.size == pageSize,
                        fromCache = false
                    )
                )
            }

            is AppResult.Error -> {
                when (result.error) {
                    is AppError.Unauthorized -> {
                        return result
                    }

                    is AppError.Forbidden -> {
                        return result
                    }

                    else -> {
                        val cachedAlerts = alertDao.getAlertsForSpace(
                            spaceName = spaceName,
                            userId = userId,
                            skip = skip,
                            limit = pageSize
                        )

                        if (cachedAlerts.isNotEmpty()) {
                            AppResult.Success(
                                AlertsPage(
                                    alerts = cachedAlerts.map { it.toDomain() },
                                    page = page,
                                    pageSize = pageSize,
                                    hasNextPage = cachedAlerts.size == pageSize,
                                    fromCache = true
                                )
                            )
                        } else {
                            AppResult.Error(result.error)
                        }
                    }
                }
            }
        }
    }
}