package io.github.qobiljon.stressapp.core.data

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query

@Dao
interface AccDataDao {
    @Query("SELECT * FROM accdata;")
    fun getAll(): List<AccData>

    @Insert
    fun insertAll(vararg acceleration: AccData)

    @Delete
    fun delete(acceleration: AccData)
}