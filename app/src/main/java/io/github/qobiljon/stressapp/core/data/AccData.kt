package io.github.qobiljon.stressapp.core.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class AccData(
    @PrimaryKey val timestamp: Long,
    @ColumnInfo(name = "x") val x: Float,
    @ColumnInfo(name = "y") val y: Float,
    @ColumnInfo(name = "z") val z: Float,
)