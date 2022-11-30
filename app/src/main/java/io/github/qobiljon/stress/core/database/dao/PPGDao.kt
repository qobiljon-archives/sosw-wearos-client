package io.github.qobiljon.stress.core.database.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import io.github.qobiljon.etagent.database.data.PPG

@Dao
interface PPGDao {
    @Query("SELECT timestamp||','||a||','||b||','||c||','||d||','||e||','||f||','||g||','||h||','||i||','||j||','||k||','||l||','||m||','||n||','||o||','||p||'\n' FROM ppg WHERE timestamp <= :lastTimestamp ORDER BY timestamp ASC")
    fun exportCSVRows(lastTimestamp: Long): List<String>

    @Query("SELECT IFNULL(MAX(timestamp), -1) timestamp FROM ppg ORDER by timestamp DESC LIMIT 1;")
    fun getLastTimestamp(): Long

    @Query("DELETE FROM ppg WHERE timestamp <= :lastTimestamp")
    fun clearItems(lastTimestamp: Long)

    @Insert
    fun insertAll(vararg ppg: PPG)

    @Delete
    fun delete(ppg: PPG)
}