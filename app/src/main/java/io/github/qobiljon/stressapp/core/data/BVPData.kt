package io.github.qobiljon.stressapp.core.data

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey


@Entity
data class BVPData(
    @PrimaryKey val timestamp: Long,
    @ColumnInfo(name = "light_intensity") val lightIntensity: Int,
)