package io.github.qobiljon.stressapp

import android.app.Application
import io.github.qobiljon.stressapp.utils.Storage


class AndroidApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        Storage.init(applicationContext)
    }
}