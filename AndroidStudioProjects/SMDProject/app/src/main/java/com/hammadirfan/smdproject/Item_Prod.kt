package com.hammadirfan.smdproject

import java.io.Serializable

data class Item_Prod(
    val id: Int,
    var name: String,
    val description: String,
    var price: Double,
    val unitsRemaining: Int,
    val imageUrl: String,
    val status: String? // For special notes like "Almost Out of Stock, Kindly Re-order"
):Serializable

