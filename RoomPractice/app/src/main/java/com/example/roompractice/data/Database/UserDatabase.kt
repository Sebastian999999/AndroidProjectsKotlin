package com.example.roompractice.data.Database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.roompractice.data.DAOS.UserDAO
import com.example.roompractice.data.Entities.User

@Database(entities = [User::class] , version = 1 , exportSchema = false)
abstract class UserDatabase : RoomDatabase(){
    abstract fun userDAO():UserDAO
    companion object {
        @Volatile //Made volatile, to avoid multiple threads with same values
                //(in this case null) creating multiple instances of the same database
        private var INSTANCE: UserDatabase? = null

        fun getUserDatabase(context: Context): UserDatabase {
            return INSTANCE ?: synchronized(this) {//Synchronized. So that only one thread can access it at a time
                val instance = Room.databaseBuilder(
                    context.applicationContext,//Should only be run in application context
                    UserDatabase::class.java,
                    "user_database"
                ).build()
                INSTANCE = instance
                instance
            }
        }
    }
}