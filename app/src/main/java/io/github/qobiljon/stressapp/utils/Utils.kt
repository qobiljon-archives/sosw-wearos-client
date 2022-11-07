package io.github.qobiljon.stressapp.utils

import android.annotation.SuppressLint
import android.content.Context
import android.widget.Toast
import java.text.SimpleDateFormat
import java.time.Instant
import java.util.*

object Utils {
    @SuppressLint("SimpleDateFormat")
    fun validDate(dateStr: String): Boolean {
        if (dateStr.length != 8) return false

        try {
            val df = SimpleDateFormat("yyyyMMdd")
            df.isLenient = false
            val date = df.parse(dateStr)
            if (date == null || !date.before(Date.from(Instant.now()))) return false
        } catch (e: Exception) {
            return false
        }

        return true
    }

    fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}