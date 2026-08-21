package com.cashierserviceapp.data.local.database

import androidx.room.Room
import androidx.room.RoomDatabase
import kotlinx.cinterop.ExperimentalForeignApi
import platform.Foundation.NSDocumentDirectory
import platform.Foundation.NSFileManager
import platform.Foundation.NSUserDomainMask

actual class DatabaseDriveFactory {
    @OptIn(ExperimentalForeignApi::class)
    actual fun createDriver(): RoomDatabase.Builder<AppDatabase> {
        val documentDirectory = NSFileManager.defaultManager.URLForDirectory(
            directory = NSDocumentDirectory,
            inDomain = NSUserDomainMask,
            appropriateForURL = null,
            create = false,
            error = null
        )?.path ?: error("Could not find Document directory")

        val dbFilePath = "$documentDirectory/${AppDatabase.DATABASE_NAME}"

        return Room.databaseBuilder<AppDatabase>(
            name = dbFilePath
        )
    }
}