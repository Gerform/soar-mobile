package tech.soc.soar.shared.data.database

import android.content.Context
import androidx.room.Room

object DatabaseFactory {

    fun create(context: Context): AppDatabase {
        return Room.databaseBuilder(
            context.applicationContext,
            AppDatabase::class.java,
            "soar_database.db"
        ).build()
    }
}