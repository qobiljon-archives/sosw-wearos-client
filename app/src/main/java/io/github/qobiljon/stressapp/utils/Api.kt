package io.github.qobiljon.stressapp.utils

import android.content.Context
import io.github.qobiljon.etagent.R
import io.github.qobiljon.stressapp.core.api.ApiInterface
import io.github.qobiljon.stressapp.core.api.requests.AuthRequest
import io.github.qobiljon.stressapp.core.api.requests.SubmitAccDataRequest
import io.github.qobiljon.stressapp.core.api.requests.SubmitBVPDataRequest
import io.github.qobiljon.stressapp.core.data.AccData
import io.github.qobiljon.stressapp.core.data.BVPData
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object Api {
    private fun getApiInterface(context: Context): ApiInterface {
        return Retrofit.Builder().addConverterFactory(GsonConverterFactory.create()).baseUrl(context.getString(R.string.api_base_url)).build().create(ApiInterface::class.java)
    }

    suspend fun authenticate(context: Context, fullName: String, dateOfBirth: String): Boolean {
        val result = getApiInterface(context).authenticate(
            AuthRequest(
                full_name = fullName,
                date_of_birth = dateOfBirth,
            )
        )
        return result.errorBody() == null
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
        } catch (e: Exception) {
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
        } catch (e: Exception) {
            false
        }
    }
}