package tech.soc.soar.shared.data.database

import android.content.Context
import androidx.room.Room

object DatabaseFactory {

    @Volatile
    private var database: AppDatabase? = null

    fun create(context: Context): AppDatabase {
        return database ?: synchronized(this) {
            database ?: Room.databaseBuilder(
                context.applicationContext,
                AppDatabase::class.java,
                "soar_database.db"
            )
                .fallbackToDestructiveMigration()
                .build()
                .also { database = it }
        }
    }
}