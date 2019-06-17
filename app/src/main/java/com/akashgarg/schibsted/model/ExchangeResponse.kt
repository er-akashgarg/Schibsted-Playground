package com.akashgarg.schibsted.model

import com.google.gson.annotations.SerializedName

data class ExchangeResponse(

    @field:SerializedName("date")
    val date: String = "",

    @field:SerializedName("success")
    val success: Boolean = false,

    @field:SerializedName("rates")
    val rates: Rates? = null,

    @field:SerializedName("historical")
    val historical: Boolean = false,

    @field:SerializedName("timestamp")
    val timestamp: Int = 0,

    @field:SerializedName("base")
    val base: String = ""
)