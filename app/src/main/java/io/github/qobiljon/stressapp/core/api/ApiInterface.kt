package io.github.qobiljon.stressapp.core.api

import io.github.qobiljon.stressapp.core.api.requests.*
import io.github.qobiljon.stressapp.core.api.responses.*
import retrofit2.Response
import retrofit2.http.*

interface ApiInterface {
    @POST("sign_in")
    suspend fun signIn(@Body request: SignInRequest): Response<SignInResponse>

    @POST("submit_off_body")
    suspend fun submitOffBodyData(@Header("Authorization") token: String, @Body submitOffBodyRequest: SubmitOffBodyRequest): Response<Void>

    @Multipart
    @POST("submit_ppg")
    suspend fun submitPPGData(@Part @Header("Authorization") token: String, @Part @Body submitPPGRequest: SubmitPPGRequest): Response<Void>

    @Multipart
    @POST("submit_acc")
    suspend fun submitAccData(@Part @Header("Authorization") token: String, @Part @Body submitAccRequest: SubmitAccRequest): Response<Void>
}