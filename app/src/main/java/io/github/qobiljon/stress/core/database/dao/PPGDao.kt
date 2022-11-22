package io.github.qobiljon.stress.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.qobiljon.etagent.database.data.PPG

@Dao
interface PPGDao {
    @Query("SELECT * FROM ppg;")
    fun getAll(): List<PPG>

    @Query("SELECT * FROM ppg ORDER BY timestamp ASC LIMIT :k")
    fun getK(k: Int): List<PPG>

    @Insert
    fun insertAll(vararg ppg: PPG)

    @Delete
    fun delete(ppg: PPG)
}