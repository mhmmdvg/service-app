package com.cashierserviceapp.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import java.io.File

actual class DatabaseDriveFactory {
    actual fun createDriver(): RoomDatabase.Builder<AppDatabase> {
        val userHome = System.getProperty("user.home")
        val appDataDir = File(userHome, ".cashierapp")

        if (!appDataDir.exists()) {
            appDataDir.mkdirs()
        }

        val dbFile = File(appDataDir, AppDatabase.DATABASE_NAME)

        return Room.databaseBuilder<AppDatabase>(name = dbFile.absolutePath)
    }
}