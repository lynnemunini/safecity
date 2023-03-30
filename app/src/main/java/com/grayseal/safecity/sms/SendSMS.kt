package com.grayseal.safecity.sms

import android.util.Base64
import android.util.Log
import com.grayseal.safecity.BuildConfig
import com.grayseal.safecity.BuildConfig.ACCOUNT_SID
import com.grayseal.safecity.BuildConfig.AUTH_TOKEN
import com.grayseal.safecity.network.TwilioAPI
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit

fun sendMessage(name: String, contact: String, policeStationName: String) {
    val body = "${policeStationName.uppercase()}\n\n" +
            "Hello $name, we would like to acknowledge that we have received your crime " +
            "report.\n\nPlease be assured that we are taking your report seriously and are " +
            "currently in the process of assigning an officer to your case.\n\nThank you for " +
            "bringing this matter to our attention."
    val from = BuildConfig.TWILIO_NUMBER

    val base64EncodedCredentials = "Basic " + Base64.encodeToString(
        ("$ACCOUNT_SID:$AUTH_TOKEN").toByteArray(), Base64.NO_WRAP
    )

    val data = HashMap<String, String>()
    data["From"] = from
    data["To"] = contact
    data["Body"] = body

    val retrofit = Retrofit.Builder()
        .baseUrl("https://api.twilio.com/2010-04-01/")
        .build()
    val api = retrofit.create(TwilioAPI::class.java)

    api.sendMessage(ACCOUNT_SID, base64EncodedCredentials, data).enqueue(object :
        Callback<ResponseBody> {
        override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
            if (response.isSuccessful) Log.d("TAG", "onResponse->success")
            else Log.d("TAG", "${response.code()}, ${response.message()}")
        }
        override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
            Log.d("TAG", "onFailure")
        }
    })
}