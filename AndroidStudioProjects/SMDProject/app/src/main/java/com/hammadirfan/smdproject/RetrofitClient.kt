package com.hammadirfan.smdproject

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "http://172.17.67.216/myapp/"  // Use your local IP or '10.0.2.2' for Android emulator

    val instance: ProductApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(ProductApiService::class.java)
    }

    val tokenInstance: TokenApiService by lazy {
        val retrofit = Retrofit.Builder()
            .baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        retrofit.create(TokenApiService::class.java)
    }
}
