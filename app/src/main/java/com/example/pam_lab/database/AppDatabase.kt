package com.example.pam_lab.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.migration.Migration
import androidx.sqlite.db.SupportSQLiteDatabase

@Database(entities = [Route::class, RouteTimer::class], version = 2)
abstract class AppDatabase: RoomDatabase() {
    abstract fun routeDao(): RouteDao
    abstract fun routeTimerDao(): RouteTimerDao

    companion object {
        @Volatile
        private var instance: AppDatabase? = null

        val MIGRATION_1_2 = object : Migration(1, 2) {
            override fun migrate(db: SupportSQLiteDatabase) {
                db.execSQL(
                    "CREATE TABLE IF NOT EXISTS `route_timer` (`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, `routeName` TEXT NOT NULL, `timeInSeconds` INTEGER NOT NULL)"
                )
            }
        }

        fun getInstance(context: Context): AppDatabase =
            instance ?: synchronized(this) {
                instance ?: Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database"
                )
                .addMigrations(MIGRATION_1_2)
                .build().also { instance = it }
            }
    }
}
