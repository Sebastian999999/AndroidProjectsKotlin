package com.example.foodsafetyapp.repository

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Log
import com.example.foodsafetyapp.models.Alert
import com.example.foodsafetyapp.models.AlertSeverity
import com.example.foodsafetyapp.utils.NotificationHelper
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream
import java.util.Date
import java.util.UUID

class AlertsRepository(private val context: Context) {
    private val auth = FirebaseAuth.getInstance()
    private val db = FirebaseFirestore.getInstance()
    private val alertsCollection = db.collection("alerts")

    suspend fun getAllAlerts(): List<Alert> = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: run {
                Log.e("AlertsRepository", "User not authenticated, returning empty alerts list")
                return@withContext emptyList<Alert>()
            }

            Log.d("AlertsRepository", "Getting alerts for user: $userId")

            val snapshot = alertsCollection
                .whereEqualTo("userId", userId)
                .orderBy("timestamp", Query.Direction.DESCENDING)
                .get()
                .await()

            Log.d("AlertsRepository", "Firestore returned ${snapshot.documents.size} alert documents")

            val alerts = mutableListOf<Alert>()

            snapshot.documents.forEach { doc ->
                try {
                    // Manually create Alert objects from document data
                    val data = doc.data
                    if (data != null) {
                        val severity = when (data["severity"] as? String) {
                            "HIGH" -> AlertSeverity.HIGH
                            "MEDIUM" -> AlertSeverity.MEDIUM
                            "LOW" -> AlertSeverity.LOW
                            else -> AlertSeverity.MEDIUM
                        }

                        val alert = Alert(
                            id = doc.id,
                            userId = data["userId"] as? String ?: "",
                            timestamp = (data["timestamp"] as? com.google.firebase.Timestamp)?.toDate() ?: Date(),
                            title = data["title"] as? String ?: "",
                            message = data["message"] as? String ?: "",
                            foodName = data["foodName"] as? String ?: "",
                            severity = severity,
                            hasBeenRead = data["hasBeenRead"] as? Boolean ?: false,
                            imageByteArray = data["imageByteArray"] as? ArrayList<Int>
                        )

                        alerts.add(alert)
                        Log.d("AlertsRepository", "Added alert: ${alert.id}, ${alert.title}")
                    }
                } catch (e: Exception) {
                    Log.e("AlertsRepository", "Error converting document to Alert: ${e.message}")
                    e.printStackTrace()
                }
            }

            Log.d("AlertsRepository", "Returning ${alerts.size} alerts")
            return@withContext alerts
        } catch (e: Exception) {
            Log.e("AlertsRepository", "Error getting alerts: ${e.message}")
            e.printStackTrace()
            return@withContext emptyList()
        }
    }

    suspend fun getUnreadAlertsCount(): Int = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val snapshot = alertsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("hasBeenRead", false)
                .get()
                .await()

            snapshot.size()
        } catch (e: Exception) {
            Log.e("AlertsRepository", "Error getting unread alerts count: ${e.message}")
            0
        }
    }

    suspend fun markAlertAsRead(alertId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            alertsCollection
                .document(alertId)
                .update("hasBeenRead", true)
                .await()
            true
        } catch (e: Exception) {
            Log.e("AlertsRepository", "Error marking alert as read: ${e.message}")
            false
        }
    }

    suspend fun markAllAlertsAsRead(): Boolean = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: throw Exception("User not authenticated")
            val snapshot = alertsCollection
                .whereEqualTo("userId", userId)
                .whereEqualTo("hasBeenRead", false)
                .get()
                .await()

            val batch = db.batch()
            snapshot.documents.forEach { doc ->
                batch.update(doc.reference, "hasBeenRead", true)
            }
            batch.commit().await()
            true
        } catch (e: Exception) {
            Log.e("AlertsRepository", "Error marking all alerts as read: ${e.message}")
            false
        }
    }

    suspend fun createAlert(
        title: String,
        message: String,
        foodName: String,
        severity: AlertSeverity,
        bitmap: Bitmap? = null
    ): String? = withContext(Dispatchers.IO) {
        try {
            val userId = auth.currentUser?.uid ?: run {
                Log.e("AlertsRepository", "User not authenticated")
                // Still show notification even if we can't save to database
                NotificationHelper.showNotification(
                    context,
                    title,
                    message,
                    severity
                )
                return@withContext null
            }

            // Convert bitmap to byte array if provided
            val byteArray = bitmap?.let {
                val baos = ByteArrayOutputStream()
                it.compress(Bitmap.CompressFormat.JPEG, 70, baos)
                val bytes = baos.toByteArray()
                ArrayList(bytes.map { byte -> byte.toInt() })
            }

            val alertId = UUID.randomUUID().toString()
            val alert = Alert(
                id = alertId,
                userId = userId,
                timestamp = Date(),
                title = title,
                message = message,
                foodName = foodName,
                severity = severity,
                hasBeenRead = false,
                imageByteArray = byteArray
            )

            // First show notification (so user gets notified even if database save fails)
            NotificationHelper.showNotification(
                context,
                title,
                message,
                severity
            )

            // Then try to save to database
            try {
                alertsCollection.document(alertId).set(alert).await()
                return@withContext alertId
            } catch (e: Exception) {
                Log.e("AlertsRepository", "Error saving alert to database: ${e.message}")
                // We already showed the notification, so just return null
                return@withContext null
            }
        } catch (e: Exception) {
            Log.e("AlertsRepository", "Error creating alert: ${e.message}")
            // Try to at least show notification
            try {
                NotificationHelper.showNotification(
                    context,
                    title,
                    message,
                    severity
                )
            } catch (e2: Exception) {
                Log.e("AlertsRepository", "Also failed to show notification: ${e2.message}")
            }
            return@withContext null
        }
    }

    suspend fun deleteAlert(alertId: String): Boolean = withContext(Dispatchers.IO) {
        try {
            alertsCollection
                .document(alertId)
                .delete()
                .await()
            true
        } catch (e: Exception) {
            Log.e("AlertsRepository", "Error deleting alert: ${e.message}")
            false
        }
    }

    fun byteArrayToBitmap(intArray: ArrayList<Int>?): Bitmap? {
        if (intArray == null) return null
        val byteArray = intArray.map { it.toByte() }.toByteArray()
        return BitmapFactory.decodeByteArray(byteArray, 0, byteArray.size)
    }
}