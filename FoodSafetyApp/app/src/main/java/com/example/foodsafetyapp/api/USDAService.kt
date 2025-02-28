import retrofit2.http.GET
import retrofit2.http.Path
import retrofit2.http.Query

interface USDAService {
    @GET("foods/search")
    suspend fun searchFood(
        @Query("query") query: String,
        @Query("api_key") apiKey: String,
        @Query("pageSize") pageSize: Int = 1,  // Limit results
        @Query("dataType") dataType: String = "Foundation,Survey (FNDDS)"  // Include detailed data
    ): USDAResponse

    @GET("food/{fdcId}")
    suspend fun getFoodDetails(
        @Path("fdcId") fdcId: String,
        @Query("api_key") apiKey: String
    ): USDAFoodDetail
}

// Add new data classes for detailed food information
data class USDAFoodDetail(
    val fdcId: String,
    val description: String,
    val foodClass: String?,
    val foodCategory: String?,
    val storageGuidelines: List<String>?,
    val safetyTips: List<String>?,
    val handlingInstructions: List<String>?
)