package tech.soc.soar.shared.data.database

import androidx.room.Database
import androidx.room.RoomDatabase
import tech.soc.soar.shared.data.account.local.UserAccountDao
import tech.soc.soar.shared.data.account.local.UserAccountEntity
import tech.soc.soar.shared.data.alert.local.AlertDao
import tech.soc.soar.shared.data.alert.local.AlertDetailsEntity
import tech.soc.soar.shared.data.alert.local.AlertEntity
import tech.soc.soar.shared.data.alert.local.AlertViewEntity
import tech.soc.soar.shared.data.response.local.ResponseDao
import tech.soc.soar.shared.data.response.local.ResponseRequestEntity
import tech.soc.soar.shared.data.response.local.SuccessfulActionEntity

@Database(
    entities = [
        UserAccountEntity::class,
        AlertEntity::class,
        AlertViewEntity::class,
        AlertDetailsEntity::class,
        ResponseRequestEntity::class,
        SuccessfulActionEntity::class
    ],
    version = 2,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {

    abstract fun userAccountDao(): UserAccountDao

    abstract fun alertDao(): AlertDao

    abstract fun responseDao(): ResponseDao
}