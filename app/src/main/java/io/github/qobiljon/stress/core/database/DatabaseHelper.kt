package io.github.qobiljon.stress.core.database

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.room.Room
import io.github.qobiljon.etagent.database.data.Acc
import io.github.qobiljon.etagent.database.data.PPG
import io.github.qobiljon.stress.R
import io.github.qobiljon.stress.core.api.ApiHelper
import io.github.qobiljon.stress.core.database.data.OffBody
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.io.File

object DatabaseHelper {
    private const val KEY_PREFS_NAME = "shared_prefs"
    private const val KEY_AUTH_TOKEN = "auth_token"

    private lateinit var db: AppDatabase

    private fun getSharedPreferences(context: Context): SharedPreferences {
        return context.getSharedPreferences(KEY_PREFS_NAME, Context.MODE_PRIVATE)
    }

    fun init(context: Context) {
        db = Room.databaseBuilder(context, AppDatabase::class.java, context.getString(R.string.room_db_name)).allowMainThreadQueries().build()
    }

    fun syncToCloud(context: Context) {
        if (!isAuthenticated(context)) return

        runBlocking {
            launch {
                val dao = db.accDao()
                val lastTimestamp = dao.getLastTimestamp()
                val exportFile = File(context.filesDir, "acc.csv")

                var canSubmit = false
                if (exportFile.exists() && exportFile.length() == 0L) exportFile.delete()
                if (lastTimestamp != -1L) {
                    if (!exportFile.exists()) exportFile.createNewFile()
                    canSubmit = true
                }
                if (canSubmit) {
                    dao.exportCSVRows(lastTimestamp).forEach { line -> exportFile.appendText(line) }
                    dao.clearItems(lastTimestamp)
                    val success = ApiHelper.submitAccFile(
                        context = context,
                        token = getAuthToken(context),
                        file = exportFile,
                    )
                    if (success) {
                        exportFile.delete()
                        dao.clearItems(lastTimestamp)
                    }
                }
            }

            launch {
                val dao = db.ppgDao()
                val lastTimestamp = dao.getLastTimestamp()
                val exportFile = File(context.filesDir, "ppg.csv")

                var canSubmit = false
                if (exportFile.exists() && exportFile.length() == 0L) exportFile.delete()
                if (lastTimestamp != -1L) {
                    if (!exportFile.exists()) exportFile.createNewFile()
                    canSubmit = true
                }
                if (canSubmit) {
                    dao.exportCSVRows(lastTimestamp).forEach { line -> exportFile.appendText(line) }
                    dao.clearItems(lastTimestamp)
                    val success = ApiHelper.submitPPGFile(
                        context = context,
                        token = getAuthToken(context),
                        file = exportFile,
                    )
                    if (success) {
                        exportFile.delete()
                        dao.clearItems(lastTimestamp)
                    }
                }
            }

            launch {
                val dao = db.offBodyDao()
                for (offBody in dao.getAll()) {
                    val success = ApiHelper.submitOffBody(
                        context = context,
                        token = getAuthToken(context),
                        offBody = offBody,
                    )
                    if (success) dao.delete(offBody)
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

    fun saveOffBodyReading(offBodyData: OffBody) {
        db.offBodyDao().insertAll(offBodyData)
    }

    fun savePPGReading(ppg: PPG) {
        db.ppgDao().insertAll(ppg)
    }

    fun saveAccReading(acc: Acc) {
        db.accDao().insertAll(acc)
    }
}
