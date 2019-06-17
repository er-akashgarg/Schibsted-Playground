package com.akashgarg.schibsted.presenter

import android.annotation.SuppressLint
import com.akashgarg.schibsted.model.ExchangeResponse
import com.akashgarg.schibsted.restclient.apis.Api
import com.akashgarg.schibsted.utils.Urls
import com.akashgarg.schibsted.view.base.BaseView
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.DisposableObserver
import io.reactivex.schedulers.Schedulers
import retrofit2.Retrofit

class ExchangeHistoryPresenter(
    private var retrofit: Retrofit,
    var baseView: BaseView,
    var date: String
) {
    init {
        baseView.showLoading()
        getPostsList()
    }

    @SuppressLint("CheckResult")
    fun getPostsList() {
        getPostsObservable()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(getObserver())
    }

    private fun getObserver(): DisposableObserver<ExchangeResponse> {
        return object : DisposableObserver<ExchangeResponse>() {

            override fun onNext(data: ExchangeResponse) {
                baseView.dismissLoading()
                baseView.successResult(data)
            }

            override fun onError(e: Throwable) {
                baseView.failure(e.message)
            }

            override fun onComplete() {
            }
        }
    }

    private fun getPostsObservable(): Observable<ExchangeResponse> {
        val api = retrofit.create(Api::class.java)
        return api.historyByDate(date, Urls.ACCESS_KEY, Urls.BASE, Urls.SYMBOLS)
    }
}