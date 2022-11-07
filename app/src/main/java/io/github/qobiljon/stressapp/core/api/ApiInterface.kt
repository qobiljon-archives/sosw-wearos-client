package io.github.qobiljon.stressapp.core.api

import io.github.qobiljon.stressapp.core.api.requests.*
import io.github.qobiljon.stressapp.core.api.responses.*
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST

interface ApiInterface {
    @POST("auth_watch")
    suspend fun authenticate(@Body request: AuthRequest): Response<AuthResponse>

    @POST("submit_bvp")
    suspend fun submitBVPData(@Body submitBVPDataRequest: SubmitBVPDataRequest): Response<Void>

    @POST("submit_acc")
    suspend fun submitAccData(@Body submitAccDataRequest: SubmitAccDataRequest): Response<Void>

    @POST("submit_off_body")
    suspend fun submitOffBodyData(@Body submitOffBodyDataRequest: SubmitOffBodyDataRequest): Response<Void>
}