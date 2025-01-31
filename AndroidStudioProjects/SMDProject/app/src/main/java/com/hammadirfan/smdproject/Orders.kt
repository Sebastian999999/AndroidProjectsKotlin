package com.hammadirfan.smdproject

data class Orders(
    val orderId: String,
    val customerName: String,
    val orderDate: String,
    val orderTime: String,
    val amount: Double,
    val type: String
)
