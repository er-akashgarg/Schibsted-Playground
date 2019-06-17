package com.akashgarg.schibsted.view.base

import com.akashgarg.schibsted.model.ExchangeResponse

interface BaseView {

    fun showLoading()
    fun dismissLoading()
    fun successResult(data: ExchangeResponse)
    fun failure(message: String?)
}