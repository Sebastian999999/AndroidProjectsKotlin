package com.example.roompractice.data.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import com.example.roompractice.data.DAOS.UserDAO
import com.example.roompractice.data.Database.UserDatabase
import com.example.roompractice.data.Entities.User
import com.example.roompractice.data.Repositories.UserRepository
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class UserViewModel(application: Application):AndroidViewModel(application) {
    val readAllData:LiveData<List<User>>
    private val repository:UserRepository

    init{
        val userDAO = UserDatabase.getUserDatabase(application).userDAO()
        repository = UserRepository(userDAO)
        readAllData = repository.readAllData
    }
    fun AddUser(user:User){
        viewModelScope.launch(Dispatchers.IO){
            repository.addUser(user)
        }
    }
}