package com.example.retrofitpractice

import androidx.lifecycle.ViewModelProvider
import com.example.retrofitpractice.Repository.Repository

class MainViewModelFactory(private val repository: Repository): ViewModelProvider.Factory {
    override fun <T : androidx.lifecycle.ViewModel> create(modelClass: Class<T>): T {
        return MainViewModel(repository) as T
    }
}