package io.github.qobiljon.stress.sensors.listeners

import android.content.Context
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.util.Log
import io.github.qobiljon.stress.ui.MainActivity
import io.github.qobiljon.stress.utils.Storage

class OffBodyListener(private val context: Context) : SensorEventListener {
    companion object {
        const val INTENT_FILTER = "watch-off-body-detection"
        const val INTENT_KEY = "isOffBody"
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.values.isEmpty()) return
        val isOffBody = event.values[0] != 1.0f
        Log.e(MainActivity.TAG, "${event.values[0]}, ${event.values[1]}")

        Storage.saveOffBodyReading(timestamp = System.currentTimeMillis(), isOffBody = isOffBody)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // not implemented
    }
}
