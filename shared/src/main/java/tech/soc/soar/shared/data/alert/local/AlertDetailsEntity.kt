package tech.soc.soar.shared.data.alert.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alert_details",
    indices = [
        Index(value = ["space_name"]),
        Index(value = ["date"])
    ]
)
data class AlertDetailsEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "space_name")
    val spaceName: String,

    @ColumnInfo(name = "raw_body")
    val rawBody: String,

    @ColumnInfo(name = "updated_at")
    val updatedAt: Long
)