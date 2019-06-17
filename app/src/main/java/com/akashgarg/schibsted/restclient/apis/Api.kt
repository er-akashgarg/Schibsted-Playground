package com.akashgarg.schibsted.restclient.apis

import com.akashgarg.schibsted.model.ExchangeResponse
import com.akashgarg.schibsted.utils.Urls
import io.reactivex.Observable
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query


interface Api {

    @POST(Urls.BASE_URL + "{date}")
    fun historyByDate(
        @Path("date") date: String
        , @Query("access_key") accessKey: String
        , @Query("base") base: String
        , @Query("symbols") symbols: String
    ): Observable<ExchangeResponse>
}
