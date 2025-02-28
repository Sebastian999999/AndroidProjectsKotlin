package com.example.foodsafetyapp.models

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class Product(
    @SerialName("id") val id: Int = 0,
    @SerialName("category_id") val category_id: Int = 0,
    @SerialName("name") val name: String = "",
    @SerialName("name_subtitle") val name_subtitle: String = "",
    @SerialName("pantry_min") val pantry_min: Int = 0,
    @SerialName("pantry_max") val pantry_max: Int = 0,
    @SerialName("pantry_metric") val pantry_metric: Int = 0,
    @SerialName("pantry_tip") val pantry_tip: String = "",
    @SerialName("refrigerate_min") val refrigerate_min: Int = 0,
    @SerialName("refrigerate_max") val refrigerate_max: Int = 0,
    @SerialName("refrigerate_metric") val refrigerate_metric: Int = 0,
    @SerialName("refrigerate_tip") val refrigerate_tip: String = "",
    @SerialName("freeze_min") val freeze_min: Int = 0,
    @SerialName("freeze_max") val freeze_max: Int = 0,
    @SerialName("freeze_metric") val freeze_metric: Int = 0,
    @SerialName("freeze_tip") val freeze_tip: String = "",
    @SerialName("tips") val tips: String = ""
)
