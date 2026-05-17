package tech.soc.soar.shared.data.alert.local

import androidx.room.ColumnInfo

data class AlertWithViewEntity(
    @ColumnInfo(name = "id")
    val id: Long,

    @ColumnInfo(name = "date")
    val date: String,

    @ColumnInfo(name = "status")
    val status: String,

    @ColumnInfo(name = "space_name")
    val spaceName: String,

    @ColumnInfo(name = "reason")
    val reason: String,

    @ColumnInfo(name = "is_viewed")
    val isViewed: Boolean?
)