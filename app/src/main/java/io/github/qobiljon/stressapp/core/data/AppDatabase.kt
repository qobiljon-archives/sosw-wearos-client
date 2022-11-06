package io.github.qobiljon.stressapp.core.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [
        AccData::class,
        BVPData::class,
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun accDataDao(): AccDataDao
    abstract fun bvpDataDao(): BVPDataDao
}