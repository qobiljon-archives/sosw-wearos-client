package io.github.qobiljon.stress.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.qobiljon.stress.core.database.data.OffBody

@Dao
interface OffBodyDao {
    @Query("SELECT * FROM offbody ORDER BY timestamp DESC")
    fun getAll(): List<OffBody>

    @Insert
    fun insertAll(vararg offBodyData: OffBody)

    @Delete
    fun delete(offBodyData: OffBody)
}