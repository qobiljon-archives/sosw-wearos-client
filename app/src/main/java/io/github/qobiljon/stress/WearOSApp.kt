package io.github.qobiljon.stress

import android.app.Application
import io.github.qobiljon.stress.utils.Storage
import io.github.qobiljon.stress.utils.Storage2


class WearOSApp : Application() {
    override fun onCreate() {
        super.onCreate()

        Storage2.init(applicationContext)
    }
}