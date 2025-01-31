package com.example.roompractice.data.DAOS

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.example.roompractice.data.Entities.User

@Dao
interface UserDAO {

    @Insert(onConflict = OnConflictStrategy.IGNORE) //This is used to resolve conflicts
                                                    //SAy we have two users with same ids and values
                                                    //then it will ignore the second one
    suspend fun addUser(user:User)

    @Query("SELECT * FROM user_table ORDER BY id ASC")
    fun getAllUserData() : LiveData<List<User>>
}