package io.github.qobiljon.stress.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import io.github.qobiljon.etagent.database.data.Acc
import io.github.qobiljon.etagent.database.data.PPG
import io.github.qobiljon.stress.core.database.dao.AccDao
import io.github.qobiljon.stress.core.database.dao.OffBodyDao
import io.github.qobiljon.stress.core.database.dao.PPGDao
import io.github.qobiljon.stress.core.database.data.OffBody

@Database(
    entities = [
        OffBody::class,
        PPG::class,
        Acc::class,
    ], version = 1
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun offBodyDao(): OffBodyDao
    abstract fun ppgDao(): PPGDao
    abstract fun accDao(): AccDao
}