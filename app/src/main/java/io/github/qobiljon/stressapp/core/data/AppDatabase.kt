package io.github.qobiljon.stressapp.core.data

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [OffBody::class], version = 1)
abstract class AppDatabase : RoomDatabase() {
    abstract fun offBodyDataDao(): OffBodyDataDao
}