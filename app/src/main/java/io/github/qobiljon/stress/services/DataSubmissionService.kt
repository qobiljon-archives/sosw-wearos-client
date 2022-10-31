package io.github.qobiljon.stress.services

import android.app.*
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Binder
import android.os.IBinder
import android.util.Log
import io.github.qobiljon.etagent.R
import io.github.qobiljon.stress.MainActivity
import io.github.qobiljon.stress.utils.Sync
import io.github.qobiljon.stress.utils.Utils
import java.io.File

class DataSubmissionService : Service() {
    // region vars
    private lateinit var _thread: Thread
    private var isRunning = false
    // endregion

    // region binder
    private val mBinder: IBinder = LocalBinder()

    inner class LocalBinder : Binder() {
        @Suppress("unused")
        val getService: DataSubmissionService
            get() = this@DataSubmissionService
    }

    override fun onBind(intent: Intent): IBinder {
        return mBinder
    }
    // endregion

    override fun onCreate() {
        Log.e(MainActivity.TAG, "DataSubmissionService.onCreate()")

        // data submission thread
        _thread = Thread {
            while (isRunning) {
                if (Utils.isOnline(applicationContext)) {
                    // submit acc data
                    val file = File(filesDir, "com.samsung.sensor.hr_raw.csv")
                    val targetFile = File(filesDir, "com.samsung.sensor_hr_raw_copy.csv")
                    if (targetFile.exists()) {
                        Sync.submitAccData(f = targetFile)
                    }
                    if (file.exists()) {
                        file.copyTo(target = targetFile, overwrite = false)
                    }

                    // submit bvp data

                    // submit off-body data

                }

                try {
                    Thread.sleep(1000)
                } catch (e: InterruptedException) {
                    break
                }
            }
        }

        // foreground svc
        val notificationId = 98763
        val notificationIntent = Intent(this, MainActivity::class.java)
        val pendingIntent = PendingIntent.getActivity(this, 0, notificationIntent, PendingIntent.FLAG_IMMUTABLE)
        val notificationChannelId = javaClass.name
        val notificationChannelName = "Stress data submission"
        val notificationChannel = NotificationChannel(notificationChannelId, notificationChannelName, NotificationManager.IMPORTANCE_DEFAULT)
        notificationChannel.lightColor = Color.BLUE
        notificationChannel.lockscreenVisibility = Notification.VISIBILITY_PUBLIC
        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(notificationChannel)
        val notification = Notification.Builder(this, notificationChannelId).setContentTitle(getString(R.string.app_name)).setContentText("Data submission in progress").setSmallIcon(R.mipmap.ic_launcher).setContentIntent(pendingIntent).build()
        startForeground(notificationId, notification)

        super.onCreate()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        Log.e(MainActivity.TAG, "DataSubmissionService.onStartCommand()")
        if (isRunning) return START_STICKY
        else {
            isRunning = true
            _thread.start()
        }

        return super.onStartCommand(intent, flags, startId)
    }

    override fun onDestroy() {
        Log.e(MainActivity.TAG, "DataSubmissionService.onDestroy()")

        isRunning = false
        _thread.interrupt()
        _thread.join()
        super.onDestroy()
    }
}
