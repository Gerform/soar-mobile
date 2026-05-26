package tech.soc.soar.shared.data.push.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface InAppNotificationDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(entity: InAppNotificationEntity)

    @Query(
        """
        SELECT *
        FROM in_app_notifications
        WHERE account_uid = :accountUid
        ORDER BY created_at DESC
        """
    )
    suspend fun getByAccountUid(
        accountUid: String
    ): List<InAppNotificationEntity>

    @Query(
        """
        DELETE FROM in_app_notifications
        WHERE account_uid = :accountUid
          AND type = :type
          AND alert_id = :alertId
        """
    )
    suspend fun deleteNewAlert(
        accountUid: String,
        type: String,
        alertId: Long
    )

    @Query(
        """
        SELECT response_request_id
        FROM in_app_notifications
        WHERE account_uid = :accountUid
          AND type = :type
          AND alert_id = :alertId
          AND response_request_id IS NOT NULL
        """
    )
    suspend fun getApprovalResponseIdsForAlert(
        accountUid: String,
        type: String,
        alertId: Long
    ): List<Long>

    @Query(
        """
        DELETE FROM in_app_notifications
        WHERE account_uid = :accountUid
          AND type = :type
          AND alert_id = :alertId
        """
    )
    suspend fun deleteApprovalRequestsForAlert(
        accountUid: String,
        type: String,
        alertId: Long
    )
}