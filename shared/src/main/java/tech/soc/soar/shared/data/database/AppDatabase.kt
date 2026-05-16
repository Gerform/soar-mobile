package tech.soc.soar.shared.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.soc.soar.shared.data.account.local.UserAccountDao
import tech.soc.soar.shared.data.account.local.UserAccountEntity

@Database(
    entities = [
        UserAccountEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userAccountDao(): UserAccountDao
}