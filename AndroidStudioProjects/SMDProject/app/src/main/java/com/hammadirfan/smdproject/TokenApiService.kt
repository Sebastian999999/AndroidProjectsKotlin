package com.hammadirfan.smdproject

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface TokenApiService {
    @FormUrlEncoded
    @POST("register_token.php")
    fun registerToken(
        @Field("token") token: String
    ): Call<ResponseBody>
}