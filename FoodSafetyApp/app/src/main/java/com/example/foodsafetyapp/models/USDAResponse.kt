data class USDAResponse(
    val foods: List<USDAFood>
) {
    data class USDAFood(
        val fdcId: String,  // Add this line
        val description: String,
        val foodNutrients: List<Nutrient>,
        val ingredients: String?,
        val foodCategory: String? = null  // Optional: add this if you want category info in search
    ) {
        data class Nutrient(
            val nutrientId: Int,
            val nutrientName: String,
            val value: Double,
            val unitName: String
        )
    }
}