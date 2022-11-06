package io.github.qobiljon.stressapp.core.api.requests

import io.github.qobiljon.stressapp.core.data.AccData

data class SubmitAccDataRequest(
    val full_name: String,
    val date_of_birth: String,
    val acc_data: List<AccData>,
)