package tech.soc.soar.shared.data.response.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "successful_actions",
    indices = [
        Index(value = ["alert_id"]),
        Index(value = ["response_request_id"]),
        Index(value = ["space_name"]),
        Index(value = ["created_at"])
    ]
)
data class SuccessfulActionEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "alert_id")
    val alertId: Long,

    @ColumnInfo(name = "response_request_id")
    val responseRequestId: Long,

    @ColumnInfo(name = "approved_user")
    val approvedUser: String,

    @ColumnInfo(name = "space_name")
    val spaceName: String,

    @ColumnInfo(name = "action_name")
    val actionName: String,

    @ColumnInfo(name = "target_value")
    val targetValue: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long
)