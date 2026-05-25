package tech.soc.soar.shared.data.push.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "mobile_push_permissions",
    indices = [
        Index(
            value = ["account_uid", "device_id"],
            unique = true
        )
    ]
)
data class MobilePushPermissionEntity(
    @PrimaryKey(autoGenerate = true)
    @ColumnInfo(name = "id")
    val id: Long = 0,

    @ColumnInfo(name = "account_uid")
    val accountUid: String,

    @ColumnInfo(name = "device_id")
    val deviceId: String,

    @ColumnInfo(name = "enabled")
    val enabled: Boolean,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)