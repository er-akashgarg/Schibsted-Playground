# Babylon Android Tech Test


This is a sample project to get the USD by date from the server. In this sample project, I would like to show how to implement MVP (Model-View-Presenter) pattern with using Dagger2 and also by Kotlin.

For the Android, with MVP, we are able to take most of logic out from the activities (or fragments). 

# What is MVP ? Why do we need?
In Android we have a problem arising from the fact that activities are closely coupled to interface and data access mechanisms.

The Model-View-Presenter pattern allows to separate the presentation layer from the logic, so that everything about how the interface works is separated from how we represent it on screen.

# Language: Kotlin : 
    implementation "org.jetbrains.kotlin:kotlin-stdlib-jdk7:$kotlin_version"

# There are some third party dependencies which is used in this application:

# SDP Dependencies for UI to All compatible devices
    implementation "com.intuit.sdp:sdp-android:$sdp_lib_version"

# DI(Dependency Injection) : Dagger2:
    implementation "com.google.dagger:dagger:$dagger_version"
    kapt "com.google.dagger:dagger-compiler:$dagger_version"
    
# Rx-java & Rx-android:
    implementation "io.reactivex.rxjava2:rxandroid:$rxandroid_version"
    implementation "io.reactivex.rxjava2:rxjava:$rxjava_version"
    implementation "com.jakewharton.retrofit:retrofit2-rxjava2-adapter:$rxjava_adapter_version"
    
# Retrofit(Network Lib):     
    implementation "com.squareup.retrofit2:retrofit:$retrofit_version"
    implementation "com.squareup.retrofit2:converter-gson:$retrofit_version"
    
# RxJava2CallAdapter
    implementation "com.squareup.retrofit2:adapter-rxjava2:$retrofit_version"    
    
# Dagger2 Lib Dependencies
     implementation "com.google.dagger:dagger:$dagger_version"
     annotationProcessor "com.google.dagger:dagger-compiler:$dagger_version"
     implementation "com.google.dagger:dagger-android:$dagger_version"
     implementation "com.google.dagger:dagger-android-support:$dagger_version"
     annotationProcessor "com.google.dagger:dagger-android-processor:$dagger_version"
     
# Proguard-: for Debug Mode:   
     minifyEnabled  true
     shrinkResources  true
     proguardFiles getDefaultProguardFile('proguard-android-optimize.txt'), 'proguard-rules.pro' 
  
# Lets try to setup MVP architecture with Kotlin, RxJava2, Retrofit, Dagger2 in app .
  
  **This project structured into following setup or packages like :**
  
  **. Retrofit:** is a Rest Client. Retrofit depend on OkHttp.
  
  **. Dagger(di):** used for dependency injection.
  
  **. RxJava:** Reactive Extensions.
  
  **. models:** Data models.
  
  **. ui:** Activities and also with presenter and contract.
  
  **. utils:** Some tweaks.
  
  
  **We need to create Api Module for Retrofit Setup**
  
  addConvertorFactory()- I used gson converter for data serialization but if you want, you can create custom convertor, use moshi library or etc.
  
  addCallAdapterFactory() support service method return types. I added RxJava2CallAdapterFactory.create()
  
  **Lets start to create the api module!**
  
     @Module
     public class ApiModule {
      private String mBaseUrl;
  
      public ApiModule(String mBaseUrl) {
          this.mBaseUrl = mBaseUrl;
      }
  
  
      @Provides
      @Singleton
      Cache provideHttpCache(Application application) {
          int cacheSize = 10 * 1024 * 1024;
          Cache cache = new Cache(application.getCacheDir(), cacheSize);
          return new Cache();
      }
  
      @Provides
      @Singleton
      Gson provideGson() {
          GsonBuilder gsonBuilder = new GsonBuilder();
          gsonBuilder.setFieldNamingPolicy(FieldNamingPolicy.LOWER_CASE_WITH_UNDERSCORES);
          return gsonBuilder.create();
      }
  
      @Provides
      @Singleton
      OkHttpClient provideOkhttpClient(Cache cache) {
          OkHttpClient.Builder client = new OkHttpClient.Builder();
         client.cache(cache);
          return client.build();
      }
  
      @Provides
      @Singleton
      Retrofit provideRetrofit(Gson gson, OkHttpClient okHttpClient) {
          return new Retrofit.Builder()
                  .addConverterFactory(GsonConverterFactory.create(gson))
                  .baseUrl(mBaseUrl)
                  .addCallAdapterFactory(RxJava2CallAdapterFactory.create())
                  .client(okHttpClient)
                  .build();
      }
  }
  
  **We need to make an api call so create Service interface. I used Observable for this.**  
  
    interface Api {
    
        @POST(Urls.BASE_URL + "{date}")
        fun historyByDate(
            @Path("date") date: String
            , @Query("access_key") accessKey: String
            , @Query("base") base: String
            , @Query("symbols") symbols: String
        ): Observable<ExchangeResponse>
     }

  
  **Now, we can go on Presenter Layer, Contract for View and Presenter interfaces…**
  
      interface BaseView {
          fun showLoading()
          fun dismissLoading()
          fun successResult(data: ExchangeResponse)
          fun failure(message: String?)
      }
     
     
 **Lets create the presenter class:**
 
    package com.akashgarg.schibsted.presenter

    import android.annotation.SuppressLint
    import com.akashgarg.schibsted.model.ExchangeResponse
    import com.akashgarg.schibsted.restclient.apis.Api
    import com.akashgarg.schibsted.utils.Urls
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
   
      

**ApplicationComponent class: Finally We can bind it with Builder.**

    import schibsted.di.module.ApiModule;
    import schibsted.di.module.AppModule;
    import schibsted.ui.activity.MainActivity;
    import dagger.Component;
    import javax.inject.Singleton;

    @Singleton
    @Component(modules = {AppModule.class, ApiModule.class})
    public interface ApiComponent {
       void inject(MainActivity activity);
     }    

  **API URL-:**  http://data.fixer.io/api/2019-06-07?access_key="asddasdxxxxxxyyyxyxzyz"
  **Method :**  POST  
  
  **Unit test With Mockito**
  
   **Unit testing** is an important part of any product development lifecycle. The main purpose of unit testing is to test components in isolation from each other and that is how our code should be written as well.
   **Mockito** can be used as a mocking framework in Android. It allows us to fake external interactions with annotation @Mock.
    A mock object is a dummy implementation for an interface or a class in which you define the output of certain method calls. Mock objects are configured to perform a certain behavior during a test.
  
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
    }
    **ScreenShot**
   ![Screenshot](https://github.com/er-akashgarg/Schibsted-Playground/blob/master/app/screenshots/s_1.png)
   ![Screenshot](https://github.com/er-akashgarg/Schibsted-Playground/blob/master/app/screenshots/s_2.png)
   
 
   

