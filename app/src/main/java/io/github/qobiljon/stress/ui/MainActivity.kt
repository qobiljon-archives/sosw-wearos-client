package io.github.qobiljon.stress.ui

import android.Manifest
import android.content.*
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.WindowManager
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.github.qobiljon.stress.R
import io.github.qobiljon.stress.databinding.ActivityMainBinding
import io.github.qobiljon.stress.sensors.listeners.OffBodyListener
import io.github.qobiljon.stress.sensors.services.DataCollectionService
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


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

    private val offBodyEventReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val isOffBody = intent.getBooleanExtra(OffBodyListener.INTENT_KEY, true)
            val tvOffBody = findViewById<TextView>(R.id.tvOffBody)

            if (isOffBody) {
                tvOffBody.text = getString(R.string.off_body)
                binding.root.background = AppCompatResources.getDrawable(applicationContext, R.drawable.orange_circle)
            } else {
                tvOffBody.text = getString(R.string.on_body)
                binding.root.background = AppCompatResources.getDrawable(applicationContext, R.drawable.green_circle)
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // binding
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
    }

    override fun onBackPressed() {

    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onResume() {
        super.onResume()

        val broadcastManager = LocalBroadcastManager.getInstance(applicationContext)
        broadcastManager.registerReceiver(offBodyEventReceiver, IntentFilter(OffBodyListener.INTENT_FILTER))

        GlobalScope.launch {
            val tvDate = findViewById<TextView>(R.id.tvDate)
            val tvTime = findViewById<TextView>(R.id.tvTime)

            while (true) {
                runOnUiThread {
                    val dateTime = DateTimeFormatter.ofPattern("MM.dd (EE), hh:mm a").format(LocalDateTime.now()).split(", ")
                    tvDate.text = dateTime[0]
                    tvTime.text = dateTime[1]
                }
                delay(1000)
            }
        }

        if (checkSelfPermission(Manifest.permission.BODY_SENSORS) == PackageManager.PERMISSION_GRANTED) {
            val collectionIntent = Intent(applicationContext, DataCollectionService::class.java)
            bindService(collectionIntent, collectSvcCon, BIND_AUTO_CREATE)
        } else requestPermissions(arrayOf(Manifest.permission.BODY_SENSORS), PERMISSION_REQUEST_CODE)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (grantResults.isEmpty()) throw java.lang.RuntimeException("Empty permission results")
        if (requestCode == PERMISSION_REQUEST_CODE) {
            Log.e("DATA", "GRANTED ${grantResults[0] == PackageManager.PERMISSION_GRANTED}")
            if (grantResults[0] == PackageManager.PERMISSION_DENIED) finishAffinity()
            else {
                val collectionIntent = Intent(applicationContext, DataCollectionService::class.java)
                bindService(collectionIntent, collectSvcCon, BIND_AUTO_CREATE)
            }
        }
    }
}
