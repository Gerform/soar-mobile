package tech.soc.soar.shared.data.alert.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index

@Entity(
    tableName = "alert_views",
    primaryKeys = ["alert_id", "user_id"],
    indices = [
        Index(value = ["user_id"]),
        Index(value = ["alert_id"]),
        Index(value = ["user_id", "is_viewed"])
    ]
)
data class AlertViewEntity(
    @ColumnInfo(name = "alert_id")
    val alertId: Long,

    @ColumnInfo(name = "user_id")
    val userId: Int,

    @ColumnInfo(name = "is_viewed")
    val isViewed: Boolean,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)