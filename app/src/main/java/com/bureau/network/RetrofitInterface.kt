package com.bureau.network

import com.bureau.models.callFilter.request.CallFilterRequest
import com.bureau.models.callFilter.request.SmsFilterRequest
import com.bureau.models.callFilter.response.CommonFilterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * Created by Abhin.
 */
interface RetrofitInterface {

    @POST("androidapi/callfilter")
    suspend fun callFilterApi(@Body requestBody: CallFilterRequest): Response<CommonFilterResponse>

    @POST("androidapi/smsfilter")
    suspend fun smsFilterApi(@Body requestBody: SmsFilterRequest): Response<CommonFilterResponse>
}

