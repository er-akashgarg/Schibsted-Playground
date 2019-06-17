package com.akashgarg.schibsted.model

import com.google.gson.annotations.SerializedName

data class Rates(

    @field:SerializedName("USD")
    val uSD: Double = 0.0
)