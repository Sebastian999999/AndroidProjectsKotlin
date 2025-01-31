package com.hammadirfan.smdproject

import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.POST

interface ProductApiService {
    @FormUrlEncoded
    @POST("add_product.php")
    fun addProduct(
        @Field("name") name: String,
        @Field("description") description: String,
        @Field("price") price: String,
        @Field("barcode") barcode: String,
        @Field("image_url") imageUrl: String
    ): Call<ResponseBody> // This should match the expected response type
}
