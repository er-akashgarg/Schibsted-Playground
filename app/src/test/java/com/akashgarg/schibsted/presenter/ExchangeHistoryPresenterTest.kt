package com.akashgarg.schibsted.presenter

import com.akashgarg.schibsted.model.ExchangeResponse
import com.akashgarg.schibsted.restclient.apis.Api
import com.akashgarg.schibsted.ui.activity.MainActivity
import com.akashgarg.schibsted.utils.Urls
import com.akashgarg.schibsted.view.base.BaseView
import com.google.gson.Gson
import io.reactivex.Observable
import io.reactivex.android.plugins.RxAndroidPlugins
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.observers.TestObserver
import io.reactivex.schedulers.Schedulers
import okhttp3.OkHttpClient
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.mockito.Mock
import org.mockito.MockitoAnnotations
import org.mockito.junit.MockitoJUnitRunner
import retrofit2.Retrofit
import retrofit2.adapter.rxjava2.RxJava2CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory


@RunWith(MockitoJUnitRunner::class)
class ExchangeHistoryPresenterTest {

    private val STRING_UNKNOWN_ERROR = "STRING_UNKNOWN_ERROR"

    @Mock
    lateinit var baseView: BaseView

    @Mock
    lateinit var mActivity: MainActivity

    lateinit var usersPostPresenter: ExchangeHistoryPresenter

    lateinit var retrofit: Retrofit

    var date: String = "2018-03-20"
    @Before
    fun setUp() {

        //init mock
        MockitoAnnotations.initMocks(this)

        RxAndroidPlugins.setInitMainThreadSchedulerHandler { scheduler -> Schedulers.trampoline() }

        retrofit = Retrofit.Builder()
            .addConverterFactory(GsonConverterFactory.create(Gson()))
            .baseUrl(Urls.BASE_URL)
            .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
            .client(OkHttpClient())
            .build()

        mActivity.showLoading()
        usersPostPresenter = ExchangeHistoryPresenter(retrofit, baseView, date)
    }


    @Test
    fun getExchangeHistory_WhenApiInvoke() {
        getPostsList()
        mActivity.dismissLoading()
    }

    private fun getPostsList() {

//        Mockito.`when`(mActivity.getString(com.akashgarg.schibsted.R.string.error_msg)).thenReturn(STRING_UNKNOWN_ERROR)

        // Create the testObserver for response
        val observer = TestObserver<ExchangeResponse>()

        getExchangeHistoryObservable()
            .subscribeOn(Schedulers.newThread())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeWith(observer)


        // dispose the Observer Or CleanUp
        observer.dispose()

    }

    private fun getExchangeHistoryObservable(): Observable<ExchangeResponse> {
        val api = retrofit.create(Api::class.java)
        return api.historyByDate(date, Urls.ACCESS_KEY, Urls.BASE, Urls.SYMBOLS)
    }
}

