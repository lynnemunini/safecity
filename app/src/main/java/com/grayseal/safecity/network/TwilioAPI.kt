package com.grayseal.safecity.network

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.*

interface TwilioAPI {
    @FormUrlEncoded
    @POST("Accounts/{ACCOUNT_SID}/Messages")
    fun sendMessage(
        @Path("ACCOUNT_SID") accountSId: String,
        @Header("Authorization") signature: String,
        @FieldMap metadata: Map<String, String>
    ): Call<ResponseBody>
}