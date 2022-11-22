package io.github.qobiljon.stress

import android.app.Application
import io.github.qobiljon.stress.core.database.DatabaseHelper


class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        DatabaseHelper.init(applicationContext)
    }
}