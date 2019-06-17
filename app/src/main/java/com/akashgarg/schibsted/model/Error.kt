package com.akashgarg.schibsted.model

import com.google.gson.annotations.SerializedName

data class Error(
    @field:SerializedName("code")
    val code: Int? = null,
    @field:SerializedName("type")
    val type: String? = null
)