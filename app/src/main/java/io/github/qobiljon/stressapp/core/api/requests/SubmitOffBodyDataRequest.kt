package io.github.qobiljon.stressapp.core.api.requests

import io.github.qobiljon.stressapp.core.data.OffBodyData

data class SubmitOffBodyDataRequest(
    val full_name: String,
    val date_of_birth: String,
    val off_body_data: List<OffBodyData>
)