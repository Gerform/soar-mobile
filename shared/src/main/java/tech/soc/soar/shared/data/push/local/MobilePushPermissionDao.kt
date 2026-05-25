package tech.soc.soar.shared.data.push.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query

@Dao
interface MobilePushPermissionDao {

    @Query(
        """
        SELECT enabled
        FROM mobile_push_permissions
        WHERE account_uid = :accountUid
          AND device_id = :deviceId
        LIMIT 1
        """
    )
    suspend fun isPushEnabledForAccountDevice(
        accountUid: String,
        deviceId: String
    ): Boolean?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsertPermission(
        entity: MobilePushPermissionEntity
    )
}