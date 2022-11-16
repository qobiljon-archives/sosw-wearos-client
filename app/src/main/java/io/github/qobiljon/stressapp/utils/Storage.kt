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
    private const val ACC_MIN_SUBMISSION_SIZE = 100 * 1024 // at least 100 KB
    private const val PPG_MIN_SUBMISSION_SIZE = 1024 * 1024 // at least 1 MB
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
            launch {
                getAccFiles(context).forEach {
                    val success = Api.submitAccFile(context, token = getAuthToken(context), file = it)
                    if (success) it.delete()
                }
            }
            launch {
                getPPGFiles(context).forEach {
                    val success = Api.submitPPGFile(context, token = getAuthToken(context), file = it)
                    if (success) it.delete()
                }
            }
            val offBodyDao = db.offBodyDataDao()
            launch {
                for (offBody in offBodyDao.getAll()) {
                    val success = Api.submitOffBody(context, token = getAuthToken(context), offBody = offBody)
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

    private fun getFiles(context: Context, search: String, ignore: String): List<File> {
        val ans = mutableListOf<File>()
        for (file in context.filesDir.listFiles { file -> file.name.contains(search) }?.toList() ?: listOf()) {
            if (file.name.equals(ignore)) continue
            else if (file.length() == 0L) file.delete()
            else ans.add(file)
        }
        ans.sortBy { it.name }
        return ans
    }

    private fun getAccFiles(context: Context): List<File> {
        val ans = getFiles(context, search = "acc", ignore = ACC_FILENAME)
        if (ans.isNotEmpty()) return ans

        // prepare current file for submission
        val newFile = File(context.filesDir, "acc${System.currentTimeMillis()}.csv")
        accFile?.let {
            // check if current file has "enough" amount of data
            if (it.length() < ACC_MIN_SUBMISSION_SIZE) return@let
            it.copyTo(newFile)
            val w = PrintWriter(it)
            w.print("")
            w.close()
        }
        return if (newFile.exists()) listOf(newFile) else listOf()
    }

    private fun getPPGFiles(context: Context): List<File> {
        val ans = getFiles(context, search = "ppg", ignore = PPG_FILENAME)
        if (ans.isNotEmpty()) return ans

        // prepare current file for submission
        val newFile = File(context.filesDir, "ppg${System.currentTimeMillis()}.csv")
        ppgFile?.let {
            // check if current file has "enough" amount of data
            if (it.length() < PPG_MIN_SUBMISSION_SIZE) return@let
            it.copyTo(newFile)
            val w = PrintWriter(it)
            w.print("")
            w.close()
        }
        return if (newFile.exists()) listOf(newFile) else listOf()
    }
}