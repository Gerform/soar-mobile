package tech.soc.soar.shared.data.alert.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "alerts",
    indices = [
        Index(value = ["space_name"]),
        Index(value = ["date"]),
        Index(value = ["space_name", "date"])
    ]
)
data class AlertEntity(
    @PrimaryKey
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "space_name")
    val spaceName: String,

    @ColumnInfo(name = "reason")
    val reason: String
)