package io.github.qobiljon.stress.core.api

import android.content.Context
import com.koushikdutta.ion.Ion
import io.github.qobiljon.stress.R
import io.github.qobiljon.stress.core.api.requests.SignInRequest
import io.github.qobiljon.stress.core.api.requests.SubmitOffBodyRequest
import io.github.qobiljon.stress.core.database.data.OffBody
import io.github.qobiljon.stress.core.database.DatabaseHelper
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.io.File
import java.net.ConnectException
import java.net.SocketTimeoutException


object ApiHelper {
    private var apiInterface: ApiInterface? = null

    private fun getApiInterface(context: Context): ApiInterface {
        if (apiInterface == null) apiInterface = Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(context.getString(R.string.api_base_url)).build().create(ApiInterface::class.java)
        return apiInterface!!
    }

    suspend fun signIn(context: Context, email: String, password: String): Boolean {
        return try {
            val result = getApiInterface(context).signIn(SignInRequest(email = email, password = password))
            val resultBody = result.body()
            if (result.errorBody() == null && result.isSuccessful && resultBody != null) {
                DatabaseHelper.setAuthToken(context, authToken = resultBody.token)
                true
            } else false
        } catch (e: ConnectException) {
            false
        } catch (e: SocketTimeoutException) {
            false
        }
    }

    suspend fun submitOffBody(context: Context, token: String, offBody: OffBody): Boolean {
        return try {
            val result = getApiInterface(context).submitOffBodyData(
                token = "Token $token", SubmitOffBodyRequest(
                    timestamp = offBody.timestamp,
                    is_off_body = offBody.is_off_body,
                )
            )
            result.errorBody() == null && result.isSuccessful
        } catch (e: ConnectException) {
            false
        } catch (e: SocketTimeoutException) {
            false
        }
    }

    suspend fun submitAccFile(context: Context, token: String, file: File): Boolean {
        val res = withContext(Dispatchers.IO) { Ion.with(context).load("${context.getString(R.string.api_base_url)}submit_acc").addHeader("Authorization", "Token $token").setMultipartFile("file", "text/plain", file).asString().get() }
        return res.isEmpty()
    }

    suspend fun submitPPGFile(context: Context, token: String, file: File): Boolean {
        val res = withContext(Dispatchers.IO) { Ion.with(context).load("${context.getString(R.string.api_base_url)}submit_ppg").addHeader("Authorization", "Token $token").setMultipartFile("file", "text/plain", file).asString().get() }
        return res.isEmpty()
    }
}