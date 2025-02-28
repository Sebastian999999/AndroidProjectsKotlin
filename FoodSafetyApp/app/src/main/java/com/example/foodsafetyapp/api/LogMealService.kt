import com.example.foodsafetyapp.models.NutritionResponse
import okhttp3.MultipartBody
import retrofit2.http.Body
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part

interface LogMealService {
    @Multipart
    @POST("v2/image/recognition/complete")
    suspend fun analyzeFood(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): LogMealResponse

    // For nutrition, we need to use dish detection first
    @Multipart
    @POST("v2/image/recognition/dish")
    suspend fun analyzeDish(
        @Header("Authorization") token: String,
        @Part image: MultipartBody.Part
    ): DishResponse

    @POST("v2/recipe/nutritionalInfo")
    suspend fun getNutrition(
        @Header("Authorization") token: String,
        @Body request: NutritionRequest
    ): NutritionResponse
}

data class DishResponse(
    val recognition_results: List<DishResult>
) {
    data class DishResult(
        val id: Int,
        val name: String,
        val prob: Double
    )
}

data class NutritionRequest(
    val image_id: Long,
    val class_id: Int,
    val food_name: String
)