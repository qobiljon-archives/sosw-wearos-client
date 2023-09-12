package io.github.qobiljon.stress.ui

import android.content.ComponentName
import android.content.Intent
import android.content.ServiceConnection
import android.os.Bundle
import android.os.IBinder
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import io.github.qobiljon.stress.databinding.ActivityMainBinding
import io.github.qobiljon.stress.sensors.services.DataCollectionService


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "stress"
        const val PERMISSION_REQUEST_CODE = 1001
    }

    private lateinit var binding: ActivityMainBinding
    private var collectSvc: DataCollectionService? = null
    private var collectSvcBound: Boolean = false
    private val collectSvcCon = object : ServiceConnection {
        override fun onServiceConnected(className: ComponentName, service: IBinder) {
            val binder = service as DataCollectionService.LocalBinder
            collectSvc = binder.getService

            if (!binder.getService.isRunning) {
                val intent = Intent(applicationContext, DataCollectionService::class.java)
                startForegroundService(intent)
            }

            collectSvcBound = true
        }

        override fun onServiceDisconnected(className: ComponentName) {
            collectSvcBound = false
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        val collectionIntent = Intent(applicationContext, DataCollectionService::class.java)
        bindService(collectionIntent, collectSvcCon, BIND_AUTO_CREATE)
    }

    override fun onBackPressed() {

    }
}
