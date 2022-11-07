package io.github.qobiljon.stressapp.utils

import android.content.Context
import io.github.qobiljon.etagent.R
import io.github.qobiljon.stressapp.core.api.ApiInterface
import io.github.qobiljon.stressapp.core.api.requests.AuthRequest
import io.github.qobiljon.stressapp.core.api.requests.SubmitAccDataRequest
import io.github.qobiljon.stressapp.core.api.requests.SubmitBVPDataRequest
import io.github.qobiljon.stressapp.core.api.requests.SubmitOffBodyDataRequest
import io.github.qobiljon.stressapp.core.data.AccData
import io.github.qobiljon.stressapp.core.data.BVPData
import io.github.qobiljon.stressapp.core.data.OffBodyData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import java.net.ConnectException
import java.net.SocketTimeoutException

object Api {
    private fun getApiInterface(context: Context): ApiInterface {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(context.getString(R.string.api_base_url)).build().create(ApiInterface::class.java)
    }

    suspend fun authenticate(context: Context, fullName: String, dateOfBirth: String): Boolean {
        return try {
            val result = getApiInterface(context).authenticate(
                AuthRequest(
                    full_name = fullName,
                    date_of_birth = dateOfBirth,
                )
            )
            result.errorBody() == null && result.isSuccessful && result.body()?.success == true
        } catch (e: ConnectException) {
            false
        } catch (e: SocketTimeoutException) {
            false
        }
    }

    suspend fun submitAccData(context: Context, fullName: String, dateOfBirth: String, accData: List<AccData>): Boolean {
        return try {
            val result = getApiInterface(context).submitAccData(
                SubmitAccDataRequest(
                    full_name = fullName,
                    date_of_birth = dateOfBirth,
                    acc_data = accData,
                )
            )
            result.errorBody() == null && result.isSuccessful
        } catch (e: ConnectException) {
            false
        } catch (e: SocketTimeoutException) {
            false
        }
    }

    suspend fun submitBVPData(context: Context, fullName: String, dateOfBirth: String, bvpData: List<BVPData>): Boolean {
        return try {
            val result = getApiInterface(context).submitBVPData(
                SubmitBVPDataRequest(
                    full_name = fullName,
                    date_of_birth = dateOfBirth,
                    bvp_data = bvpData,
                )
            )
            result.errorBody() == null && result.isSuccessful
        } catch (e: ConnectException) {
            false
        } catch (e: SocketTimeoutException) {
            false
        }
    }

    suspend fun submitOffBodyData(context: Context, fullName: String, dateOfBirth: String, offBodyData: List<OffBodyData>): Boolean {
        return try {
            val result = getApiInterface(context).submitOffBodyData(
                SubmitOffBodyDataRequest(
                    full_name = fullName,
                    date_of_birth = dateOfBirth,
                    off_body_data = offBodyData,
                )
            )
            result.errorBody() == null && result.isSuccessful
        } catch (e: ConnectException) {
            false
        } catch (e: SocketTimeoutException) {
            false
        }
    }
}