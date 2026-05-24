package tech.soc.soar.shared.data.response.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "response_requests",
    indices = [
        Index(value = ["alert_id"]),
        Index(value = ["space_name"]),
        Index(value = ["status"]),
        Index(value = ["created_at"])
    ]
)
data class ResponseRequestEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "alert_id")
    val alertId: Long,

    @ColumnInfo(name = "created_by_user")
    val createdByUser: String,

    @ColumnInfo(name = "space_name")
    val spaceName: String,

    @ColumnInfo(name = "action_name")
    val actionName: String,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "target_value")
    val targetValue: String,

    @ColumnInfo(name = "request_body_json")
    val requestBodyJson: String,

    @ColumnInfo(name = "message")
    val message: String,

    @ColumnInfo(name = "created_at")
    val createdAt: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: String,

    @ColumnInfo(name = "cached_at")
    val cachedAt: Long
)