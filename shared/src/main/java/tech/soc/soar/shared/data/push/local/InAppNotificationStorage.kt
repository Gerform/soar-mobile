package tech.soc.soar.shared.data.push.local

import android.content.Context
import tech.soc.soar.shared.data.database.DatabaseFactory

object InAppNotificationStorage {

    const val TYPE_NEW_ALERT = "new_alert"
    const val TYPE_APPROVAL_REQUEST = "approval_request"

    suspend fun recordNewAlert(
        context: Context,
        accountUid: String,
        eventId: String,
        spaceName: String,
        alertId: Long
    ) {
        val database = DatabaseFactory.create(context)

        database.inAppNotificationDao().upsert(
            InAppNotificationEntity(
                eventId = eventId,
                accountUid = accountUid,
                type = TYPE_NEW_ALERT,
                spaceName = spaceName,
                alertId = alertId,
                responseRequestId = null,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun recordApprovalRequest(
        context: Context,
        accountUid: String,
        eventId: String,
        spaceName: String,
        alertId: Long,
        responseRequestId: Long
    ) {
        val database = DatabaseFactory.create(context)

        database.inAppNotificationDao().upsert(
            InAppNotificationEntity(
                eventId = eventId,
                accountUid = accountUid,
                type = TYPE_APPROVAL_REQUEST,
                spaceName = spaceName,
                alertId = alertId,
                responseRequestId = responseRequestId,
                createdAt = System.currentTimeMillis()
            )
        )
    }

    suspend fun getByAccountUid(
        context: Context,
        accountUid: String
    ): List<InAppNotificationEntity> {
        val database = DatabaseFactory.create(context)

        return database.inAppNotificationDao()
            .getByAccountUid(accountUid)
    }

    suspend fun clearNewAlert(
        context: Context,
        accountUid: String,
        alertId: Long
    ) {
        val database = DatabaseFactory.create(context)

        database.inAppNotificationDao().deleteNewAlert(
            accountUid = accountUid,
            type = TYPE_NEW_ALERT,
            alertId = alertId
        )
    }

    suspend fun getApprovalResponseIdsForAlert(
        context: Context,
        accountUid: String,
        alertId: Long
    ): Set<Long> {
        val database = DatabaseFactory.create(context)

        return database.inAppNotificationDao()
            .getApprovalResponseIdsForAlert(
                accountUid = accountUid,
                type = TYPE_APPROVAL_REQUEST,
                alertId = alertId
            )
            .toSet()
    }

    suspend fun clearApprovalRequestsForAlert(
        context: Context,
        accountUid: String,
        alertId: Long
    ) {
        val database = DatabaseFactory.create(context)

        database.inAppNotificationDao().deleteApprovalRequestsForAlert(
            accountUid = accountUid,
            type = TYPE_APPROVAL_REQUEST,
            alertId = alertId
        )
    }
}