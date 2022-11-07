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
import io.github.qobiljon.stressapp.R
import io.github.qobiljon.stressapp.core.data.AccData
import io.github.qobiljon.stressapp.core.data.BVPData
import io.github.qobiljon.stressapp.ui.MainActivity
import io.github.qobiljon.stressapp.utils.Storage
import java.util.*


class MotionHRService : Service(), SensorEventListener {
    companion object {
        private const val SENSOR_HR = "com.samsung.sensor.hr_raw"
        private const val SENSOR_BVP_COLUMN = 5 // i.e. column-f
        private const val SENSOR_ACC = Sensor.STRING_TYPE_ACCELEROMETER
        private const val SAMPLING_RATE = SensorManager.SENSOR_DELAY_GAME
    }

    private lateinit var sensorManager: SensorManager
    private var isRunning = false
    private val mBinder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        @Suppress("unused")
        val getService: MotionHRService
            get() = this@MotionHRService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onCreate() {
        Log.e(MainActivity.TAG, "MotionHRService.onCreate()")

        sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager

        // foreground svc
        val notificationId = 98764
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notificationChannelId = javaClass.name
        val notificationChannelName = "Motion and HR data collection"
        val notificationChannel = NotificationChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        val notification = Notification.Builder(this, notificationChannelId).setContentTitle(getString(R.string.app_name)).setContentText("Collecting motion and HR data...").setSmallIcon(R.mipmap.ic_stress_app).setContentIntent(pendingIntent).build()
        startForeground(notificationId, notification)

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(MainActivity.TAG, "MotionHRService.onStartCommand()")
        if (isRunning) return START_STICKY
        else {
            val allSensors = sensorManager.getSensorList(Sensor.TYPE_ALL)
            for (sensorType in listOf(SENSOR_ACC, SENSOR_HR)) {
                val sensor = allSensors.find { s -> s.stringType.equals(sensorType) }
                sensorManager.registerListener(this, sensor, SAMPLING_RATE)
            }
            isRunning = true
        }

        return START_STICKY
    }

    override fun onDestroy() {
        Log.e(MainActivity.TAG, "MotionHRService.onDestroy()")
        sensorManager.unregisterListener(this)
        isRunning = false
        super.onDestroy()
    }

    override fun onSensorChanged(event: SensorEvent) {
        if (event.values.isEmpty()) return

        when (event.sensor.stringType) {
            SENSOR_ACC -> Storage.saveAccData(
                AccData(
                    timestamp = System.currentTimeMillis(),
                    x = event.values[0],
                    y = event.values[1],
                    z = event.values[2],
                )
            )
            SENSOR_HR -> Storage.saveBVPData(
                BVPData(
                    timestamp = System.currentTimeMillis(),
                    light_intensity = event.values[SENSOR_BVP_COLUMN],
                )
            )
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
        // TODO("Not yet implemented")
    }
}
