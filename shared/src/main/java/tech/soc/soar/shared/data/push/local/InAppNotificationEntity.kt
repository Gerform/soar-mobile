package tech.soc.soar.shared.data.push.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "in_app_notifications",
    indices = [
        Index(value = ["account_uid"]),
        Index(value = ["type"]),
        Index(value = ["space_name"]),
        Index(value = ["alert_id"]),
        Index(value = ["response_request_id"])
    ]
)
data class InAppNotificationEntity(
    @PrimaryKey
    @ColumnInfo(name = "event_id")
    val eventId: String,

    @ColumnInfo(name = "account_uid")
    val accountUid: String,

    @ColumnInfo(name = "type")
    val type: String,

    @ColumnInfo(name = "space_name")
    val spaceName: String,

    @ColumnInfo(name = "alert_id")
    val alertId: Long,

    @ColumnInfo(name = "response_request_id")
    val responseRequestId: Long?,

    @ColumnInfo(name = "created_at")
    val createdAt: Long
)