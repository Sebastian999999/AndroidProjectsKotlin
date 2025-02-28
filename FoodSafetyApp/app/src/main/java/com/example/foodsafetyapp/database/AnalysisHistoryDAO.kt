package com.example.foodsafetyapp.database

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.example.foodsafetyapp.models.AnalysisHistory

@Dao
interface AnalysisHistoryDao {
    @Insert
    suspend fun saveAnalysis(analysis: AnalysisHistory)

    @Query("SELECT * FROM analysis_history ORDER BY timestamp DESC")
    suspend fun getAllAnalyses(): List<AnalysisHistory>
}