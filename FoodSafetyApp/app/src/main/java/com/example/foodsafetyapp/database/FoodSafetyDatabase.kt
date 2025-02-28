package com.example.foodsafetyapp.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.example.foodsafetyapp.models.AnalysisHistory

// database/FoodSafetyDatabase.kt
@Database(
    entities = [AnalysisHistory::class],
    version = 1
)
abstract class FoodSafetyDatabase : RoomDatabase() {
    abstract fun analysisHistoryDao(): AnalysisHistoryDao
}


