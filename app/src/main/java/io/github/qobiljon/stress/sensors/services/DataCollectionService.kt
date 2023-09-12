package io.github.qobiljon.stress.sensors.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Binder
import android.os.IBinder
import android.util.Log
import com.samsung.android.service.health.tracking.ConnectionListener
import com.samsung.android.service.health.tracking.HealthTracker
import com.samsung.android.service.health.tracking.HealthTracker.TrackerError
import com.samsung.android.service.health.tracking.HealthTracker.TrackerEventListener
import com.samsung.android.service.health.tracking.HealthTrackerException
import com.samsung.android.service.health.tracking.HealthTrackingService
import com.samsung.android.service.health.tracking.data.DataPoint
import com.samsung.android.service.health.tracking.data.HealthTrackerType
import com.samsung.android.service.health.tracking.data.Value
import io.github.qobiljon.stress.R
import io.github.qobiljon.stress.ui.MainActivity
import io.github.qobiljon.stress.utils.Storage2


class DataCollectionService : Service() {
    private val mBinder: IBinder = LocalBinder()
    private val listeners = mutableListOf<SensorEventListener>()
    var isRunning = false
    private lateinit var healthTracking: HealthTrackingService
    private var ppgGreenTracker: HealthTracker? = null
    private val trackerListener: TrackerEventListener = object : TrackerEventListener {
        override fun onDataReceived(list: List<DataPoint>) {
            // save data to file
            for (dataPoint in list) {
                val timestamp = dataPoint.timestamp
                val tmp: Array<Any> = dataPoint.b.values.toTypedArray()
                val values = IntArray(tmp.size)
                for (i in tmp.indices) {
                    values[i] = (tmp[i] as Value<Int?>).value ?: 0
                }
                Storage2.savePPGReading(timestamp, values[0])
            }
        }

        override fun onError(error: TrackerError) {
            Log.e(MainActivity.TAG, "MotionHRService.onError(): $error")
        }

        override fun onFlushCompleted() {
            ppgGreenTracker!!.flush()
        }
    }

    private val connectionListener: ConnectionListener = object : ConnectionListener {
        override fun onConnectionSuccess() {
            ppgGreenTracker = healthTracking.getHealthTracker(HealthTrackerType.PPG_GREEN)
            ppgGreenTracker?.setEventListener(trackerListener)
            Log.e(MainActivity.TAG, "MotionHRService.onConnectionSuccess()")
        }

        override fun onConnectionEnded() {}

        override fun onConnectionFailed(e: HealthTrackerException) {}
    }

    inner class LocalBinder : Binder() {
        @Suppress("unused")
        val getService: DataCollectionService
            get() = this@DataCollectionService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }

    override fun onCreate() {
        Log.e(MainActivity.TAG, "MotionHRService.onCreate()")

        // foreground svc
        val notificationId = 98764
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent =
            PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notificationChannelId = javaClass.name
        val notificationChannelName = "Motion and HR data collection"
        val notificationChannel = NotificationChannel(
            notificationChannelId,
            notificationChannelName,
            NotificationManager.IMPORTANCE_DEFAULT
        )
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        val notification = Notification.Builder(this, notificationChannelId)
            .setContentTitle(getString(R.string.app_name))
            .setContentText("Collecting motion and HR data...").setSmallIcon(R.mipmap.ic_stress_app)
            .setContentIntent(pendingIntent).build()
        startForeground(notificationId, notification)

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent, flags: Int, startId: Int): Int {
        Log.e(MainActivity.TAG, "MotionHRService.onStartCommand()")
        if (isRunning) return START_STICKY
        else isRunning = true

        // Connect HealthTrackingService
        healthTracking = HealthTrackingService(connectionListener, this)
        healthTracking.connectService()

        return START_STICKY
    }

    override fun onDestroy() {
        Log.e(MainActivity.TAG, "MotionHRService.onDestroy()")

        val sensorManager = getSystemService(Context.SENSOR_SERVICE) as SensorManager
        listeners.forEach { sensorManager.unregisterListener(it) }
        listeners.clear()
        isRunning = false

        super.onDestroy()
    }
}
