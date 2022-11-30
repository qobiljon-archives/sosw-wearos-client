package io.github.qobiljon.etagent.database.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class PPG(
    @PrimaryKey val timestamp: Long,
    @ColumnInfo(name = "a") val a: Float?,
    @ColumnInfo(name = "b") val b: Float?,
    @ColumnInfo(name = "c") val c: Float?,
    @ColumnInfo(name = "d") val d: Float?,
    @ColumnInfo(name = "e") val e: Float?,
    @ColumnInfo(name = "f") val f: Float?,
    @ColumnInfo(name = "g") val g: Float?,
    @ColumnInfo(name = "h") val h: Float?,
    @ColumnInfo(name = "i") val i: Float?,
    @ColumnInfo(name = "j") val j: Float?,
    @ColumnInfo(name = "k") val k: Float?,
    @ColumnInfo(name = "l") val l: Float?,
    @ColumnInfo(name = "m") val m: Float?,
    @ColumnInfo(name = "n") val n: Float?,
    @ColumnInfo(name = "o") val o: Float?,
    @ColumnInfo(name = "p") val p: Float?,
)