package com.example.retrofitpractice.Repository

import com.example.retrofitpractice.Models.Post
import com.example.retrofitpractice.api.RetrofitInstance
import retrofit2.Response

class Repository {
    suspend fun getPost():Response<Post>{
        return RetrofitInstance.api.getPost()
    }

    suspend fun getPost2(number:Int):Response<Post>{
        return RetrofitInstance.api.getPost2(number)
    }

    suspend fun getPosts():Response<List<Post>>{
        return RetrofitInstance.api.getPosts()
    }

    suspend fun getUserPosts(userId:Int) : Response<List<Post>>{
        return RetrofitInstance.api.getUserPosts(userId)
    }

    suspend fun getUserPostsWithMultipleQueries(userId : Int , sort : String , order : String)
    : Response<List<Post>>{
        return RetrofitInstance.api.getUserPostsWithMultipleQueries(userId, sort, order)
    }

    suspend fun getUserPostsWithMapQuery(userId:Int , options:Map<String , String>)
    : Response<List<Post>>{
        return RetrofitInstance.api.getUserPostsWithMapQuery(userId , options)
    }
}