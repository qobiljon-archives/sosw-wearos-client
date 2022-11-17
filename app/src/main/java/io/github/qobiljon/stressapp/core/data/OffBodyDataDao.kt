package io.github.qobiljon.stressapp.core.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface OffBodyDataDao {
    @Query("SELECT * FROM offbody;")
    fun getAll(): List<OffBody>

    @Query("SELECT * FROM offbody ORDER BY timestamp ASC LIMIT :k")
    fun getK(k: Int): List<OffBody>

    @Query("SELECT is_off_body FROM offbody ORDER BY timestamp DESC LIMIT 1")
    fun getLatestState(): Boolean?
    
    @Insert
    fun insertAll(vararg offBodyData: OffBody)

    @Delete
    fun delete(offBodyData: OffBody)
}