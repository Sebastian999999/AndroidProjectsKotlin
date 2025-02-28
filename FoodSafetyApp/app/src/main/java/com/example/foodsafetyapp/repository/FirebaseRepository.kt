package com.example.foodsafetyapp.repository

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.foodsafetyapp.ResultsFragment
import com.example.foodsafetyapp.ResultsFragmentArgs
import com.example.foodsafetyapp.models.HistoryEntry
import com.example.foodsafetyapp.models.HistoryEntryWithBitmap
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.storage.FirebaseStorage
import com.google.firebase.storage.storageMetadata
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class FirebaseRepository {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val storage = FirebaseStorage.getInstance()

    private val historyCollection = db.collection("history")
    private val imagesRef = storage.reference.child("food_images")

    suspend fun saveToHistory(
        args: ResultsFragmentArgs,
        bitmap: Bitmap
    ): String {
        return withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

                // Convert Bitmap to byte array
                val baos = ByteArrayOutputStream()
                bitmap.compress(Bitmap.CompressFormat.JPEG, 70, baos) // Reduced quality to save space
                val byteArray = baos.toByteArray()
                val intArray = ArrayList(byteArray.map { it.toInt() })

                // Create history entry
                val historyEntry = HistoryEntry(
                    userId = userId,
                    timestamp = System.currentTimeMillis(),
                    foodName = args.foodName,
                    confidence = args.confidence,
                    calories = args.calories,
                    protein = args.protein,
                    carbs = args.carbs,
                    fat = args.fat,
                    isSafe = args.isSafe,
                    freshnessScore = args.freshnessScore,
                    spoilageDetails = args.spoilageDetails.toList(),
                    allergens = args.allergens.toList(),
                    storageTemp = args.storageTemp,
                    storageMethod = args.storageMethod,
                    handlingTips = args.handlingTips.toList(),
                    shelfLife = args.shelfLife,
                    safetyWarnings = args.safetyWarnings.toList(),
                    recommendedAction = args.recommendedAction,
                    imageByteArray = intArray
                )

                historyCollection.add(historyEntry).await()
                "History saved successfully"
            } catch (e: Exception) {
                throw Exception("Failed to save history: ${e.message}")
            }
        }
    }

    // Function to convert byte array back to Bitmap when fetching
    private fun byteArrayToBitmap(intArray: ArrayList<Int>): Bitmap {
        val byteArray = intArray.map { it.toByte() }.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }

    // Function to fetch history with images
    suspend fun getUserHistory(searchQuery: String = ""): List<HistoryEntry> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")

                // Simple query matching how we save
                val snapshot = historyCollection
                    .whereEqualTo("userId", userId)
                    .get()
                    .await()

                val historyEntries = snapshot.toObjects(HistoryEntry::class.java)

                // Filter by search query if needed
                if (searchQuery.isNotEmpty()) {
                    historyEntries.filter {
                        it.foodName.contains(searchQuery, ignoreCase = true)
                    }
                } else {
                    historyEntries
                }
            } catch (e: Exception) {
                Log.e("Firebase", "Error fetching history: ${e.message}")
                throw e
            }
        }
    }

    suspend fun getRecentHistory(maxItems: Int = 5, daysAgo: Int = 3): List<HistoryEntry> {
        return withContext(Dispatchers.IO) {
            try {
                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
                val threeDaysAgo = System.currentTimeMillis() - (daysAgo * 24 * 60 * 60 * 1000)

                val snapshot = historyCollection
                    .whereEqualTo("userId", userId)
                    .whereGreaterThan("timestamp", threeDaysAgo)
                    .orderBy("timestamp", Query.Direction.DESCENDING)
                    .limit(maxItems.toLong())
                    .get()
                    .await()

                snapshot.toObjects(HistoryEntry::class.java)
            } catch (e: Exception) {
                Log.e("Firebase", "Error fetching recent history: ${e.message}")
                emptyList()
            }
        }
    }


    // Add method to delete history entry
//    suspend fun deleteHistoryEntry(entryId: String) {
//        withContext(Dispatchers.IO) {
//            try {
//                val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
//
//                // Get the entry first to check ownership and get image URL
//                val entry = historyCollection.document(entryId).get().await()
//                    .toObject(HistoryEntry::class.java)
//
//                if (entry?.userId == userId) {
//                    // Delete image from storage if exists
//                    if (!entry.imageUrl.isNullOrEmpty()) {
//                        storage.getReferenceFromUrl(entry.imageUrl).delete().await()
//                    }
//
//                    // Delete document from Firestore
//                    historyCollection.document(entryId).delete().await()
//                } else {
//                    throw Exception("Unauthorized to delete this entry")
//                }
//            } catch (e: Exception) {
//                throw Exception("Failed to delete history entry: ${e.message}")
//            }
//        }
//    }
}