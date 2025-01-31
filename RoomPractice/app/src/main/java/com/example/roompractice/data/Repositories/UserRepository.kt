package com.example.roompractice.data.Repositories

import androidx.lifecycle.LiveData
import com.example.roompractice.data.DAOS.UserDAO
import com.example.roompractice.data.Entities.User

class UserRepository (private val userDAO: UserDAO){
    val readAllData:LiveData<List<User>> = userDAO.getAllUserData()

    suspend fun addUser(user:User){
        userDAO.addUser(user)
    }
    
}