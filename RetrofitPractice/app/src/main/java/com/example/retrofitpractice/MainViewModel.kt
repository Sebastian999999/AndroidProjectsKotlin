package com.example.retrofitpractice

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.retrofitpractice.Models.Post
import com.example.retrofitpractice.Repository.Repository
import com.example.retrofitpractice.api.RetrofitInstance
import kotlinx.coroutines.launch
import retrofit2.Response

class MainViewModel(private val repository: Repository): ViewModel() {
    val myResponse : MutableLiveData<Response<Post>> = MutableLiveData()
    val myResponse2:MutableLiveData<Response<Post>> = MutableLiveData()
    val myResponse3:MutableLiveData<Response<List<Post>>> = MutableLiveData()
    val userPostsResponse:MutableLiveData<Response<List<Post>>> = MutableLiveData()
    fun getPost(){
         viewModelScope.launch {
            val response = repository.getPost()
            myResponse.postValue(response)
        }
    }

    fun getPost2(number:Int){
        viewModelScope.launch{
            val response = repository.getPost2(number)
            myResponse2.postValue(response)
        }
    }

    fun getPosts() {
        viewModelScope.launch{
            val response = repository.getPosts()
            myResponse3.postValue(response)
        }
    }

    fun getUserPosts(userId : Int){
        viewModelScope.launch{
            val response = repository.getUserPosts(userId)
            userPostsResponse.postValue(response)
        }
    }

    fun getUserPostsWithMultipleQueries(userId: Int , sort: String , order:String){
        viewModelScope.launch{
            val response = repository.getUserPostsWithMultipleQueries(userId , sort , order)
            userPostsResponse.postValue(response)
        }
    }

    fun getUserPostsWithMapQuery(userId:Int , options:Map<String,String>){
        viewModelScope.launch{
            val response = repository.getUserPostsWithMapQuery(userId , options)
            userPostsResponse.postValue(response)
        }
    }
}