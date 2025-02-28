package com.example.foodsafetyapp.models

import android.graphics.Bitmap

data class HistoryEntryWithBitmap(
    val entry: HistoryEntry,
    val bitmap: Bitmap
)