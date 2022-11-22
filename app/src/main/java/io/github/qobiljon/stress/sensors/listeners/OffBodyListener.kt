package io.github.qobiljon.stress.sensors.listeners

import android.content.Context
import android.content.Intent
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.github.qobiljon.stress.core.database.DatabaseHelper
import io.github.qobiljon.stress.core.database.data.OffBody

class OffBodyListener(private val context: Context) : SensorEventListener {
    companion object {
        const val INTENT_FILTER = "watch-off-body-detection"
        const val INTENT_KEY = "isOffBody"
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.values.isEmpty()) return
        val isOffBody = event.values[0] != 1.0f

        DatabaseHelper.saveOffBodyReading(OffBody(timestamp = System.currentTimeMillis(), is_off_body = isOffBody))

        val intent = Intent(INTENT_FILTER)
        intent.putExtra(INTENT_KEY, isOffBody)
        val broadcastManager = LocalBroadcastManager.getInstance(context)
        broadcastManager.sendBroadcast(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // not implemented
    }
}
