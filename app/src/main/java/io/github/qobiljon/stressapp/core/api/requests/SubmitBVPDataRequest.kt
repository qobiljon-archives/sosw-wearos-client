package io.github.qobiljon.stressapp.core.api.requests

import io.github.qobiljon.stressapp.core.data.BVPData

data class SubmitBVPDataRequest(
    val full_name: String,
    val date_of_birth: String,
    val bvp_data: List<BVPData>
)