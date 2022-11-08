package io.github.qobiljon.stressapp.core.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OffBodyDataDao {
    @Query("SELECT * FROM offbodydata;")
    fun getAll(): List<OffBodyData>

    @Query("SELECT * FROM offbodydata ORDER BY timestamp ASC LIMIT :k")
    fun getK(k: Int): List<OffBodyData>

    @Insert
    fun insertAll(vararg offBodyData: OffBodyData)

    @Delete
    fun delete(offBodyData: OffBodyData)
}