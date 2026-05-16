package tech.soc.soar.shared.data.account.local

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_accounts")
data class UserAccountEntity(
    @PrimaryKey
    @ColumnInfo(name = "uid")
    val uid: Int,

    @ColumnInfo(name = "username")
    val username: String,

    @ColumnInfo(name = "mail")
    val mail: String,

    @ColumnInfo(name = "roles_json")
    val rolesJson: String,

    @ColumnInfo(name = "last_conn")
    val lastConnection: String?,

    @ColumnInfo(name = "two_factor_confirmed")
    val twoFactorConfirmed: Boolean,

    @ColumnInfo(name = "last_login_at")
    val lastLoginAt: Long,

    @ColumnInfo(name = "is_last_used")
    val isLastUsed: Boolean
)