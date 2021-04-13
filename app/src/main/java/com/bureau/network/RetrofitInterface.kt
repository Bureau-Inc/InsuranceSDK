package com.bureau.network

import com.bureau.models.callFilter.request.CallFilterRequest
import com.bureau.models.callFilter.response.CallFilterResponse
import retrofit2.Response
import retrofit2.http.Body
import retrofit2.http.POST


/**
 * Created by Abhin.
 */
interface RetrofitInterface {

    @POST("androidapi/callfilter")
    suspend fun callFilterApi(@Body mCallFilterRequest: CallFilterRequest): Response<CallFilterResponse>
}

