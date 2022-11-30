package io.github.qobiljon.stress.core.api.requests

data class SubmitOffBodyRequest(
    val timestamp: Long,
    val is_off_body: Boolean,
)