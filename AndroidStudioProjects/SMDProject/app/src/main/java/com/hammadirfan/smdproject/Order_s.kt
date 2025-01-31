package com.hammadirfan.smdproject

import java.io.Serializable

data class Order_s(val orderId: String, val items: MutableList<Item_s>) : Serializable
