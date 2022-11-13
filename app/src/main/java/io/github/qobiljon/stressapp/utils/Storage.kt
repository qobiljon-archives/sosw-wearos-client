package io.github.qobiljon.stressapp.utils

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.room.Room
import io.github.qobiljon.stressapp.R
import io.github.qobiljon.stressapp.core.data.AppDatabase
import io.github.qobiljon.stressapp.core.data.OffBody
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File
import java.io.PrintWriter

object Storage {
    private const val KEY_PREFS_NAME = "shared_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"
    private const val ACC_FILENAME = "acc.csv"
    private const val PPG_FILENAME = "ppg.csv"
    private var accFile: File? = null
    private var ppgFile: File? = null

    private lateinit var db: AppDatabase

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(KEY_PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun init(context: Context) {
        db = Room.databaseBuilder(context, AppDatabase::class.java, context.getString(R.string.room_db_name)).allowMainThreadQueries().build()

        accFile = File(context.filesDir, ACC_FILENAME)
        if (!accFile!!.exists()) accFile!!.createNewFile()
        ppgFile = File(context.filesDir, PPG_FILENAME)
        if (!accFile!!.exists()) accFile!!.createNewFile()
    }

    fun syncToCloud(context: Context) {
        if (!isAuthenticated(context)) return

        runBlocking {
            val accFiles = getAccFiles(context)
            if (accFiles.isNotEmpty()) launch {
                Api.submitAccData(
                    context,
                    token = getAuthToken(context),
                    files = accFiles,
                )
            }

            val ppgFiles = getPPGFiles(context)
            if (ppgFiles.isNotEmpty()) launch {
                Api.submitPPGData(
                    context,
                    token = getAuthToken(context),
                    files = ppgFiles,
                )
            }

            val offBodyDao = db.offBodyDataDao()
            launch {
                for (offBody in offBodyDao.getAll()) {
                    val success = Api.submitOffBody(
                        context,
                        token = getAuthToken(context),
                        offBody = offBody,
                    )
                    if (success) offBodyDao.delete(offBody)
                }
            }
        }
    }

    fun isAuthenticated(context: Context): Boolean {
        return getSharedPreferences(context).getString(KEY_AUTH_TOKEN, null) != null
    }

    private fun getAuthToken(context: Context): String {
        return getSharedPreferences(context).getString(KEY_AUTH_TOKEN, null)!!
    }

    fun setAuthToken(context: Context, authToken: String) {
        getSharedPreferences(context).edit {
            putString(KEY_AUTH_TOKEN, authToken)
        }
    }

    fun saveOffBodyData(offBodyData: OffBody) {
        db.offBodyDataDao().insertAll(offBodyData)
    }

    fun saveAccData(timestamp: Long, x: Float, y: Float, z: Float) {
        accFile?.appendText("$timestamp,$x,$y,$z\n")
    }

    fun savePPGData(timestamp: Long, lightIntensities: FloatArray) {
        ppgFile?.appendText("$timestamp,${lightIntensities.joinToString(separator = ",")}\n")
    }

    private fun getAccFiles(context: Context): List<File> {
        // copy current file
        accFile?.copyTo(File(context.filesDir, "acc${System.currentTimeMillis()}.csv"))
        accFile?.let {
            val w = PrintWriter(it)
            w.print("")
            w.close()
        }

        // gather all acc files
        val ans = mutableListOf<File>()
        for (file in context.filesDir.listFiles { file -> file.name.contains("acc") }?.toList() ?: listOf()) {
            if (file.name.equals(ACC_FILENAME)) continue
            else if (file.length() == 0L) file.delete()
            else ans.add(file)
        }
        return ans
    }

    private fun getPPGFiles(context: Context): List<File> {
        // copy current file
        ppgFile?.copyTo(File(context.filesDir, "ppg${System.currentTimeMillis()}.csv"))
        ppgFile?.let {
            val w = PrintWriter(it)
            w.print("")
            w.close()
        }

        // gather all ppg files
        val ans = mutableListOf<File>()
        for (file in context.filesDir.listFiles { file -> file.name.contains("ppg") }?.toList() ?: listOf()) {
            if (file.name.equals(PPG_FILENAME)) continue
            else if (file.length() == 0L) file.delete()
            else ans.add(file)
        }
        return ans
    }
}