package io.github.qobiljon.stressapp.core.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface BVPDataDao {
    @Query("SELECT * FROM bvpdata;")
    fun getAll(): List<BVPData>

    @Query("SELECT * FROM bvpdata ORDER BY timestamp ASC LIMIT :k")
    fun getK(k: Int): List<BVPData>

    @Insert
    fun insertAll(vararg bvpData: BVPData)

    @Delete
    fun delete(bvpData: BVPData)
}