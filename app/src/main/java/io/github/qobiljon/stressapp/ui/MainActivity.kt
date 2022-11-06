package io.github.qobiljon.stressapp.ui

import android.content.*
import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.LinearLayout
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.content.res.AppCompatResources
import androidx.lifecycle.lifecycleScope
import androidx.localbroadcastmanager.content.LocalBroadcastManager
import io.github.qobiljon.etagent.R
import io.github.qobiljon.etagent.databinding.ActivityMainBinding
import io.github.qobiljon.stressapp.core.services.sensors.OffBodyService
import io.github.qobiljon.stressapp.utils.Api
import io.github.qobiljon.stressapp.utils.Storage
import io.github.qobiljon.stressapp.utils.Utils
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter


class MainActivity : AppCompatActivity() {
    companion object {
        const val TAG = "EasyTrackAgent"
    }

    private lateinit var binding: ActivityMainBinding
    private lateinit var llAuthentication: LinearLayout
    private lateinit var llDateTime: LinearLayout
    private var isRunning = false

    private val offBodyEventReceiver: BroadcastReceiver = object : BroadcastReceiver() {
        override fun onReceive(context: Context?, intent: Intent) {
            val isOffBody = intent.getBooleanExtra("isOffBody", true)
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

        llAuthentication = findViewById(R.id.llAuthentication)
        llDateTime = findViewById(R.id.llDateTime)

        val etFullName = findViewById<EditText>(R.id.etFullName)
        val etDateOfBirth = findViewById<EditText>(R.id.etDateOfBirth)
        val btnAuthenticate = findViewById<Button>(R.id.btnAuthenticate)
        btnAuthenticate.setOnClickListener {
            val fullName = etFullName.text.toString()
            val dateOfBirth = etDateOfBirth.text.toString()
            if (fullName.length >= 3 && Utils.validDate(dateOfBirth)) {
                lifecycleScope.launch {
                    val success = Api.authenticate(
                        applicationContext,
                        fullName = fullName,
                        dateOfBirth = dateOfBirth,
                    )
                    if (success) {
                        Storage.setFullName(applicationContext, fullName = fullName)
                        Storage.setDateOfBirth(applicationContext, dateOfBirth = dateOfBirth)
                        Utils.toast(applicationContext, getString(R.string.auth_success))

                        if (Storage.isAuthenticated(applicationContext)) {
                            llAuthentication.visibility = View.GONE
                            llDateTime.visibility = View.VISIBLE
                            isRunning = true
                            runServices()
                        }
                    } else Utils.toast(applicationContext, getString(R.string.auth_failure))
                }
            }
        }
    }

    override fun onResume() {
        super.onResume()

        if (Storage.isAuthenticated(applicationContext) && !isRunning) {
            llAuthentication.visibility = View.GONE
            llDateTime.visibility = View.VISIBLE
            isRunning = true
            runServices()
        }
    }

    @OptIn(DelicateCoroutinesApi::class)
    private fun runServices() {
        // off-body service
        startForegroundService(Intent(applicationContext, OffBodyService::class.java))
        LocalBroadcastManager.getInstance(applicationContext).registerReceiver(offBodyEventReceiver, IntentFilter("off-body-event"))

        GlobalScope.launch {
            val tvDate = findViewById<TextView>(R.id.tvDate)
            val tvTime = findViewById<TextView>(R.id.tvTime)

            while (true) {
                runOnUiThread {
                    val dateTime = DateTimeFormatter.ofPattern("EE MM.dd, KK:mm a").format(LocalDateTime.now()).split(", ")
                    tvDate.text = dateTime[0]
                    tvTime.text = dateTime[1]
                }
                delay(1000)
            }
        }
    }
}
