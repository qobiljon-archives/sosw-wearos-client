package io.github.qobiljon.stressapp.core.api.requests

data class AuthRequest(
    val full_name: String,
    val date_of_birth: String,
)