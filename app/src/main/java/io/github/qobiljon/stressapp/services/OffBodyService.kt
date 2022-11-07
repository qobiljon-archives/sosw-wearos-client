package io.github.qobiljon.stressapp.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.github.qobiljon.stressapp.R
import io.github.qobiljon.stressapp.core.data.OffBodyData
import io.github.qobiljon.stressapp.ui.MainActivity
import io.github.qobiljon.stressapp.utils.Storage
import java.util.*


class OffBodyService : Service(), SensorEventListener {
    companion object {
        private const val SENSOR_OFF_BODY = "com.samsung.sensor.low_power_offbody_detector"
        private const val SAMPLING_RATE = SensorManager.SENSOR_DELAY_NORMAL
    }

    private lateinit var sensorManager: SensorManager
    private var isRunning = false
    private val mBinder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        @Suppress("unused")
        val getService: OffBodyService
            get() = this@OffBodyService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onCreate() {
        Log.e(MainActivity.TAG, "OffBodyService.onCreate()")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // foreground svc
        val notificationId = 98765
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notificationChannelId = javaClass.name
        val notificationChannelName = "On/off-body detection"
        val notificationChannel = NotificationChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        val notification = Notification.Builder(this, notificationChannelId).setContentTitle(getString(R.string.app_name)).setContentText("Monitoring smartwatch on/off-body events").setSmallIcon(R.mipmap.ic_stress_app).setContentIntent(pendingIntent).build()
        startForeground(notificationId, notification)

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(MainActivity.TAG, "OffBodyService.onStartCommand()")
        if (isRunning) return START_STICKY
        else {
            val allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
            val sensor = allSensors.find { s -> s.stringType.equals(SENSOR_OFF_BODY) }
            sensorManager.registerListener(this, sensor, SAMPLING_RATE)
            isRunning = true
        }
        return START_STICKY
    }

    override fun onSensorChanged(event: SensorEvent) {
        val isOffBody = event.values[0] != 1.0f

        Storage.saveOffBodyData(
            OffBodyData(
                timestamp = System.currentTimeMillis(),
                is_off_body = isOffBody,
            )
        )

        if (isOffBody) stopService(Intent(applicationContext, MotionHRService::class.java))
        else startForegroundService(Intent(applicationContext, MotionHRService::class.java))

        val intent = Intent("off-body-event")
        intent.putExtra("isOffBody", isOffBody)
        LocalBroadcastManager.getInstance(applicationContext).sendBroadcast(intent)
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // TODO("Not yet implemented")
    }
}