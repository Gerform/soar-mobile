package tech.soc.soar.shared.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.soc.soar.shared.data.account.local.UserAccountDao
import tech.soc.soar.shared.data.account.local.UserAccountEntity
import tech.soc.soar.shared.data.alert.local.AlertDao
import tech.soc.soar.shared.data.alert.local.AlertEntity
import tech.soc.soar.shared.data.alert.local.AlertViewEntity

@Database(
    entities = [
        UserAccountEntity::class,
        AlertEntity::class,
        AlertViewEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userAccountDao(): UserAccountDao

    abstract fun alertDao(): AlertDao
}