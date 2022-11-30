package io.github.qobiljon.stress.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.qobiljon.etagent.database.data.Acc

@Dao
interface AccDao {
    @Query("SELECT timestamp||','||x||','||y||','||z||'\n' FROM acc WHERE timestamp <= :lastTimestamp ORDER BY timestamp ASC")
    fun exportCSVRows(lastTimestamp: Long): List<String>

    @Query("SELECT IFNULL(MAX(timestamp), -1) timestamp FROM acc ORDER by timestamp DESC LIMIT 1;")
    fun getLastTimestamp(): Long

    @Query("DELETE FROM acc WHERE timestamp <= :lastTimestamp")
    fun clearItems(lastTimestamp: Long)

    @Insert
    fun insertAll(vararg acc: Acc)

    @Delete
    fun delete(acc: Acc)
}