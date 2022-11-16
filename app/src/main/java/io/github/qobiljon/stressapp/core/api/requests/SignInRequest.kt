package io.github.qobiljon.stressapp.core.api.requests

data class SignInRequest(
    val email: String,
    val password: String,
)