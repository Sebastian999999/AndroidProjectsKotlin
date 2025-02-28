import com.example.foodsafetyapp.api.ChompService
import com.example.foodsafetyapp.api.SpoonacularService
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

// api/ApiClient.kt
object ApiClient {
    const val LOGMEAL_BASE_URL = "https://api.logmeal.es/"
    const val USDA_BASE_URL = "https://api.nal.usda.gov/fdc/v1/"
    const val CHOMP_BASE_URL = "https://chomp-food-nutrition-database-v2.p.rapidapi.com/"
    const val SPOONACULAR_BASE_URL = "https://api.spoonacular.com/"
    const val LOGMEAL_API_KEY = "f2932ea9c9215c0877b51b1795f3e7aca20c1712"
    const val USDA_API_KEY = "AXWcrzfQ9wTGTnSjvRB9tBG4TETJ2FZXjpyibkLm"
    const val CHOMP_API_KEY = "e1341b67dfmshd6994ef5058a96bp1a93e3jsn5898fe78e551"  // Get from RapidAPI

    const val SPOONACULAR_API_KEY = "4f42a5a0704f4ce6bafb481a24120169"  // Replace with your key

    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }


    private val okHttpClient = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    private val spoonacularRetrofit = Retrofit.Builder()
        .baseUrl(SPOONACULAR_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val logMealRetrofit = Retrofit.Builder()
        .baseUrl(LOGMEAL_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val usdaRetrofit = Retrofit.Builder()
        .baseUrl(USDA_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    private val chompRetrofit = Retrofit.Builder()
        .baseUrl(CHOMP_BASE_URL)
        .client(okHttpClient)
        .addConverterFactory(GsonConverterFactory.create())
        .build()

    val logMealService: LogMealService = logMealRetrofit.create(LogMealService::class.java)
    val usdaService: USDAService = usdaRetrofit.create(USDAService::class.java)
    val spoonacularService: SpoonacularService = spoonacularRetrofit.create(SpoonacularService::class.java)
    val chompService: ChompService = chompRetrofit.create(ChompService::class.java)
}