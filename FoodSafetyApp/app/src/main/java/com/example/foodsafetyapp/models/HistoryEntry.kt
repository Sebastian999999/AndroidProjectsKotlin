package com.example.foodsafetyapp.models

import com.google.firebase.firestore.PropertyName

data class HistoryEntry(
    val id: String = "", // Firebase document ID
    val userId: String = "", // Firebase Auth user ID
    val timestamp: Long = System.currentTimeMillis(),

    // Basic Food Info
    val foodName: String = "",
    val confidence: Float = 0f,

    // Nutritional Information
    val calories: Float = 0f,
    val protein: Float = 0f,
    val carbs: Float = 0f,
    val fat: Float = 0f,

    // Safety Information
    @PropertyName("safe")
    val isSafe: Boolean? = null,
    val freshnessScore: Int = 0,
    val spoilageDetails: List<String> = listOf(),
    val allergens: List<String> = listOf(),

    // Storage and Handling
    val storageTemp: String = "",
    val storageMethod: String = "",
    val handlingTips: List<String> = listOf(),
    val shelfLife: String = "",
    val safetyWarnings: List<String> = listOf(),
    val recommendedAction: String = "",

    // Image
    val imageByteArray: ArrayList<Int> = ArrayList() // Store image as byte array
) {
    constructor() : this("") // Required for Firestore
}