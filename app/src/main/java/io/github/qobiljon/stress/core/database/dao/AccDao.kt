package io.github.qobiljon.stress.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.qobiljon.etagent.database.data.Acc

@Dao
interface AccDao {
    @Query("SELECT * FROM acc;")
    fun getAll(): List<Acc>

    @Query("SELECT * FROM acc ORDER BY timestamp ASC LIMIT :k")
    fun getK(k: Int): List<Acc>

    @Insert
    fun insertAll(vararg acc: Acc)

    @Delete
    fun delete(acc: Acc)
}