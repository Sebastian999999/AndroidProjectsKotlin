data class LogMealResponse(
    val foodFamily: List<FoodItem>,
    val recognition_results: List<RecognitionResult>,
    val imageId: Long,
    val model_versions: ModelVersions,
    val occasion: String,
    val occasion_info: OccasionInfo
) {
    data class FoodItem(
        val id: Int,
        val name: String,
        val prob: Double
    )

    data class RecognitionResult(
        val id: Int,
        val name: String,
        val prob: Double,
        val subclasses: List<Subclass>
    )

    data class Subclass(
        val id: Int,
        val name: String,
        val prob: Double
    )

    data class ModelVersions(
        val drinks: String,
        val foodType: String,
        val foodgroups: String,
        val foodrec: String,
        val ingredients: String
    )

    data class OccasionInfo(
        val id: Int?,
        val translation: String
    )
}