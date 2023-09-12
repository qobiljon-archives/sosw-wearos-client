package io.github.qobiljon.stress.utils

import android.content.Context
import java.io.File

object Storage2 {
    private lateinit var ppgFile: File

    fun init(context: Context) {
        ppgFile = File(context.filesDir, "ppg_green.csv")
        if (!ppgFile.exists()) ppgFile.createNewFile()
    }

    fun savePPGReading(timestamp: Long, value: Int) {
        ppgFile.appendText("$timestamp,$value\n")
    }
}
