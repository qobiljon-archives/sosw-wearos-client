package io.github.qobiljon.stressapp.utils

import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.widget.Toast
import java.util.*

object Utils {
    fun isOnline(context: Context): Boolean {
        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val capabilities = connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
        if (capabilities != null) {
            if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR)) return true
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI)) return true
            else if (capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET)) return true
        }
        return false
    }

    fun validDate(dateStr: String): Boolean {
        if (dateStr.length != 8) return false

        try {
            val year = Integer.parseInt(dateStr.substring(0, 4))
            val month = Integer.parseInt(dateStr.substring(4, 6))
            val day = Integer.parseInt(dateStr.substring(6, 8))
            Date(year, month, day)
        } catch (e: Exception) {
            return false
        }

        return true
    }

    fun toast(context: Context, message: String) {
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }
}