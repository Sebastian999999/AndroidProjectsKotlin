package com.example.foodsafetyapp.repository

import ApiClient
import DishResponse
import LogMealResponse
import NutritionRequest
import USDAResponse
import android.content.Context
//import android.content.Context
import android.graphics.Bitmap
import android.util.Log
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
import com.example.foodsafetyapp.FoodAnalyzer
import com.example.foodsafetyapp.api.ChompNutritionInfo
import com.example.foodsafetyapp.api.ChompResponse
import com.example.foodsafetyapp.api.SpoonacularAnalyzeResponse
import com.example.foodsafetyapp.models.AlertSeverity
import com.example.foodsafetyapp.models.AnalysisHistory
import com.example.foodsafetyapp.models.FoodAnalysisResult
import com.example.foodsafetyapp.models.ImageIdRequest
import com.example.foodsafetyapp.models.IngredientsResponse

import com.example.foodsafetyapp.models.NutritionInfo
import com.example.foodsafetyapp.models.NutritionResponse
import com.example.foodsafetyapp.models.SafetyInfo
import com.example.foodsafetyapp.models.SafetyRecommendations
import com.example.foodsafetyapp.models.SpoilageDetector
import okhttp3.MediaType
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import java.io.File

// repository/FoodAnalysisRepository.kt
class FoodAnalysisRepository(private val context: Context) {
    private val TAG = "FoodAnalysisRepo"
    private val spoilageDetector = SpoilageDetector()
    private var logMealResult: LogMealResponse? = null
    private var chompResult: ChompResponse? = null
    private val commonAllergens = mutableListOf(
        "milk", "egg", "fish", "shellfish", "tree nuts",
        "peanuts", "wheat", "soybean", "dairy", "nuts" , "bread"
    )
    suspend fun generateSafetyAlert(result: FoodAnalysisResult, bitmap: Bitmap?) {
        // Only create alerts for unsafe foods
        if (!result.safetyInfo.isSafe) {
            val alertsRepository = AlertsRepository(context)

            val severity = when (result.safetyInfo.freshnessScore) {
                in 0..30 -> AlertSeverity.HIGH
                in 31..60 -> AlertSeverity.MEDIUM
                else -> AlertSeverity.LOW
            }

            val title = when (severity) {
                AlertSeverity.HIGH -> "Critical Safety Risk: ${result.foodName}"
                AlertSeverity.MEDIUM -> "Safety Warning: ${result.foodName}"
                AlertSeverity.LOW -> "Safety Notice: ${result.foodName}"
            }

            val message = buildString {
                append(result.safetyInfo.recommendedAction)
                append("\n\n")
                append("Issues detected: ")
                append(result.safetyInfo.spoilageDetails.joinToString(", "))
            }

            // Create alert in database and show notification
            alertsRepository.createAlert(
                title = title,
                message = message,
                foodName = result.foodName,
                severity = severity,
                bitmap = bitmap
            )
        }
    }
    suspend fun analyzeFoodImage(bitmap: Bitmap): FoodAnalysisResult {
        val file = createImageFile(bitmap)

        try {
            val response = analyzeWithSpoonacular(file)

            // Run your existing spoilage detection
            val spoilageResult = spoilageDetector.detectSpoilage(bitmap)

            // Extract allergens from detected ingredients
            val allergensFound = response.annotations?.let {
                extractAllergensFromIngredients(it)
            } ?: emptyList()

            return FoodAnalysisResult(
                foodName = response.category.name,  // Now using category.name
                confidence = response.category.probability.toFloat(),  // Using actual probability
                nutrition = NutritionInfo(
                    calories = response.nutrition.calories.value.toFloat(),
                    protein = response.nutrition.protein.value.toFloat(),
                    carbs = response.nutrition.carbs.value.toFloat(),
                    fat = response.nutrition.fat.value.toFloat(),
                    allergens = allergensFound
                ),
                safetyInfo = SafetyInfo(
                    isSafe = !spoilageResult.isSpoiled,
                    freshnessScore = ((1 - spoilageResult.confidence) * 100).toInt(),
                    spoilageDetails = spoilageResult.details,
                    recommendedAction = spoilageResult.recommendedAction
                ),
                image = bitmap,
                allergens = allergensFound
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error analyzing with Spoonacular", e)
            throw e
        }
    }

    suspend fun getFoodSafetyRecommendations(foodName: String): SafetyRecommendations {
        try {
            // Search for the food item
            val searchResponse = ApiClient.usdaService.searchFood(
                query = foodName,
                apiKey = ApiClient.USDA_API_KEY
            )

            // Get the first matching food item
            val foodItem = searchResponse.foods.firstOrNull()
                ?: return getDefaultRecommendations(foodName)

            // Get detailed information
            // Handle potential different fdcId types
            val fdcId = foodItem.fdcId.toString()

            val details = ApiClient.usdaService.getFoodDetails(
                fdcId = fdcId,
                apiKey = ApiClient.USDA_API_KEY
            )

            return SafetyRecommendations(
                storageTemp = getStorageTemperature(details.foodCategory),
                storageMethod = details.storageGuidelines?.firstOrNull()
                    ?: getDefaultStorageMethod(details.foodCategory),
                handlingTips = details.handlingInstructions ?: getDefaultHandlingTips(details.foodCategory),
                shelfLife = getShelfLife(details.foodCategory),
                safetyWarnings = details.safetyTips ?: getDefaultSafetyTips(details.foodCategory)
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error getting USDA recommendations", e)
            return getDefaultRecommendations(foodName)
        }
    }

    private fun getStorageTemperature(foodCategory: String?): String {
        return when (foodCategory?.lowercase()) {
            "meat", "poultry", "seafood" -> "Below 40°F (4°C)"
            "dairy", "eggs" -> "33-40°F (1-4°C)"
            "fruits", "vegetables" -> "33-45°F (1-7°C)"
            "frozen foods" -> "0°F (-18°C) or below"
            "bread", "baked goods" -> "Room temperature or below 77°F (25°C)"
            "canned foods" -> "50-70°F (10-21°C)"
            "oils", "nuts" -> "60-70°F (15-21°C)"
            else -> "Room temperature or refrigerated based on package instructions"
        }
    }

    private fun getDefaultStorageMethod(foodCategory: String?): String {
        return when (foodCategory?.lowercase()) {
            "meat", "poultry", "seafood" ->
                "Store in sealed containers on bottom shelf of refrigerator"
            "dairy" ->
                "Keep in original packaging in main refrigerator compartment"
            "fruits" ->
                "Store in produce drawer, some fruits separate to prevent ripening"
            "vegetables" ->
                "Store in produce drawer with proper humidity control"
            "bread", "baked goods" ->
                "Store in airtight container at room temperature or freeze"
            "canned foods" ->
                "Store in cool, dry place. Once opened, transfer to sealed container"
            "oils" ->
                "Store in dark, cool place away from heat sources"
            "nuts", "seeds" ->
                "Store in airtight container in cool, dry place or refrigerate"
            "eggs" ->
                "Keep in original carton on refrigerator shelf, not in door"
            "frozen foods" ->
                "Keep in original packaging in freezer, avoid freezer door storage"
            else ->
                "Store according to package instructions in appropriate container"
        }
    }

    private fun getDefaultHandlingTips(foodCategory: String?): List<String> {
        return when (foodCategory?.lowercase()) {
            "meat", "poultry" -> listOf(
                "Wash hands thoroughly before and after handling",
                "Use separate cutting boards and utensils",
                "Never wash raw meat/poultry",
                "Cook to safe internal temperature",
                "Use meat thermometer to check doneness"
            )
            "seafood" -> listOf(
                "Keep seafood refrigerated until preparation",
                "Wash hands before and after handling",
                "Cook fish until it flakes easily",
                "Dispose of any shellfish that don't open during cooking"
            )
            "dairy" -> listOf(
                "Check expiration dates regularly",
                "Keep dairy products sealed when not in use",
                "Don't return unused portions to original container",
                "Avoid leaving dairy products at room temperature"
            )
            "fruits", "vegetables" -> listOf(
                "Wash thoroughly before consuming or preparing",
                "Use separate cutting boards from meat",
                "Cut away any damaged or bruised areas",
                "Refrigerate cut produce within 2 hours"
            )
            "bread", "baked goods" -> listOf(
                "Check for signs of mold before consuming",
                "Keep wrapped or in sealed container",
                "Slice with clean knife on clean surface",
                "Toast frozen bread directly from freezer"
            )
            "eggs" -> listOf(
                "Wash hands after handling raw eggs",
                "Cook eggs until yolks are firm",
                "Don't use eggs with cracked shells",
                "Don't wash eggs before storing"
            )
            else -> listOf(
                "Wash hands before handling food",
                "Check for signs of spoilage before use",
                "Follow package instructions for handling",
                "Use clean utensils and surfaces"
            )
        }
    }

    private fun getShelfLife(foodCategory: String?): String {
        return when (foodCategory?.lowercase()) {
            "meat" -> "Raw: 1-2 days refrigerated\nCooked: 3-4 days refrigerated"
            "poultry" -> "Raw: 1-2 days refrigerated\nCooked: 3-4 days refrigerated"
            "seafood" -> "Fresh: 1-2 days refrigerated\nCooked: 3-4 days refrigerated"
            "dairy" -> "Milk: 5-7 days\nCheese: 2-4 weeks (hard), 1-2 weeks (soft)"
            "fruits" -> "3-7 days refrigerated (varies by type)"
            "vegetables" -> "3-5 days refrigerated (varies by type)"
            "bread" -> "Room temp: 5-7 days\nRefrigerated: 2 weeks\nFrozen: 3 months"
            "eggs" -> "3-5 weeks refrigerated"
            "canned foods" -> "1-4 years unopened\n3-7 days refrigerated after opening"
            "frozen foods" -> "3-6 months frozen (varies by type)"
            else -> "Check package for specific shelf life guidelines"
        }
    }

    private fun getDefaultSafetyTips(foodCategory: String?): List<String> {
        return when (foodCategory?.lowercase()) {
            "meat", "poultry" -> listOf(
                "Do not consume if color or smell seems off",
                "Cook to minimum internal temperature (165°F/74°C for poultry, 145°F/63°C for beef)",
                "Avoid cross-contamination with other foods",
                "Thaw in refrigerator, never on counter",
                "Dispose if left at room temperature over 2 hours"
            )
            "seafood" -> listOf(
                "Check for fresh smell and clear eyes in whole fish",
                "Avoid consuming raw unless sushi-grade",
                "Cook until internal temperature reaches 145°F (63°C)",
                "Discard shellfish that don't open during cooking"
            )
            "dairy" -> listOf(
                "Check for unusual odors or texture changes",
                "Discard if signs of mold present (except certain cheeses)",
                "Don't consume if left unrefrigerated over 2 hours",
                "Check expiration dates before consuming"
            )
            "fruits", "vegetables" -> listOf(
                "Discard if showing signs of mold or decay",
                "Wash thoroughly before consuming",
                "Cut away bruised areas",
                "Don't consume if unusual odor or texture"
            )
            "bread", "baked goods" -> listOf(
                "Check for mold before consuming",
                "Discard entire loaf if any mold is found",
                "Don't consume stale or hard bread",
                "Check for unusual odors"
            )
            "eggs" -> listOf(
                "Don't consume raw or undercooked eggs",
                "Discard eggs with cracked shells",
                "Don't use eggs past expiration date",
                "Check for unusual odors"
            )
            else -> listOf(
                "Check for signs of spoilage before consuming",
                "Follow package safety instructions",
                "When in doubt, throw it out",
                "Maintain proper storage temperature"
            )
        }
    }

    private fun getDefaultRecommendations(foodName: String): SafetyRecommendations {
        // Default recommendations based on food categories
        return when {
            foodName.contains("meat", ignoreCase = true) -> SafetyRecommendations(
                storageTemp = "Below 40°F (4°C)",
                storageMethod = "Keep refrigerated in sealed container",
                handlingTips = listOf(
                    "Wash hands before handling",
                    "Use separate cutting boards",
                    "Cook to safe internal temperature"
                ),
                shelfLife = "1-2 days in refrigerator",
                safetyWarnings = listOf(
                    "Do not consume if color changes",
                    "Cook thoroughly before consuming"
                )
            )
            // Add more categories...
            else -> SafetyRecommendations(
                storageTemp = "Room temperature or refrigerated",
                storageMethod = "Store in a cool, dry place",
                handlingTips = listOf("Wash before consuming", "Check for signs of spoilage"),
                shelfLife = "Varies by product",
                safetyWarnings = listOf("Inspect before consuming")
            )
        }
    }

    suspend fun analyzeFoodImageWithLogMeal(bitmap: Bitmap): FoodAnalysisResult {
        val file = createImageFile(bitmap)

        try {
            // 1. Get initial recognition
            logMealResult = analyzeWithLogMeal(file)

            // Get best match
            val bestResult = logMealResult!!.recognition_results
                .maxByOrNull { it.prob }

            // 2. Get dish-specific details
            val dishResult = analyzeDish(file)
            Log.d(TAG, "Dish result: $dishResult")

            // 3. Get nutrition info
            val nutritionInfo = getNutritionInfo(
                imageId = logMealResult!!.imageId,
                classId = bestResult?.id ?: 0,
                foodName = bestResult?.name ?: "Unknown Food"
            )

            // 4. Analyze food safety
            val spoilageResult = spoilageDetector.detectSpoilage(bitmap)

            val foodName = bestResult?.name ?: "Unknown Food"
            val allergensfound = extractAllergensFromRecognition(logMealResult!!)
            Log.d(TAG, "Allergens: $allergensfound")

            return FoodAnalysisResult(
                foodName = foodName,
                confidence = bestResult?.prob?.toFloat() ?: 0f,
                nutrition = nutritionInfo,
                safetyInfo = SafetyInfo(
                    isSafe = !spoilageResult.isSpoiled,
                    freshnessScore = ((1 - spoilageResult.confidence) * 100).toInt(),
                    spoilageDetails = spoilageResult.details,
                    recommendedAction = spoilageResult.recommendedAction
                ),
                image = bitmap,
                allergens = allergensfound
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error in LogMeal fallback", e)
            throw e
        }
    }

    private suspend fun analyzeWithSpoonacular(file: File): SpoonacularAnalyzeResponse {
        try {
            // Create image part with specific content type
            val requestBody = RequestBody.create(
                "image/jpeg".toMediaTypeOrNull(),
                file
            )

            val imagePart = MultipartBody.Part.createFormData(
                name = "file", // Spoonacular expects "file" as the part name
                filename = "food.jpg",
                body = requestBody
            )

            return ApiClient.spoonacularService.analyzeFoodImage(
                apiKey = ApiClient.SPOONACULAR_API_KEY,
                image = imagePart
            )
        } catch (e: Exception) {
            Log.e(TAG, "Error in analyzeWithSpoonacular: ${e.message}")
            throw e
        }
    }

    private fun extractAllergensFromIngredients(ingredients: List<String>): List<String> {
        val potentialAllergens = mutableSetOf<String>()
        ingredients.forEach { ingredient ->
            checkForAllergens(ingredient, potentialAllergens)
        }
        return potentialAllergens.toList()
    }

    private suspend fun analyzeWithChomp(file: File): ChompResponse {
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("image", "food.jpg", requestBody)

        return ApiClient.chompService.analyzeFood(
            apiKey = ApiClient.CHOMP_API_KEY,
            image = imagePart
        )
    }

    private suspend fun analyzeWithLogMeal(file: File): LogMealResponse {
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("image", "food.jpg", requestBody)

        return ApiClient.logMealService.analyzeFood(
            "Bearer ${ApiClient.LOGMEAL_API_KEY}",
            imagePart
        )
    }

    private suspend fun analyzeDish(file: File): DishResponse {
        val requestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
        val imagePart = MultipartBody.Part.createFormData("image", "food.jpg", requestBody)

        return ApiClient.logMealService.analyzeDish(
            "Bearer ${ApiClient.LOGMEAL_API_KEY}",
            imagePart
        )
    }

    private fun getNutritionInfoWithChomp(chompNutrition: ChompNutritionInfo): NutritionInfo {
        return NutritionInfo(
            calories = chompNutrition.calories.toFloat(),
            protein = chompNutrition.protein.toFloat(),
            carbs = chompNutrition.carbs.toFloat(),
            fat = chompNutrition.fat.toFloat(),
            allergens = extractAllergensFromIngredients(chompResult!!.ingredients)
        )
    }

    private suspend fun getNutritionInfo(imageId: Long, classId: Int, foodName: String): NutritionInfo {
        try {
            val nutritionResponse = ApiClient.logMealService.getNutrition(
                "Bearer ${ApiClient.LOGMEAL_API_KEY}",
                NutritionRequest(
                    image_id = imageId,
                    class_id = classId,
                    food_name = foodName
                )
            )

            return if (nutritionResponse.hasNutritionalInfo) {
                NutritionInfo(
                    calories = nutritionResponse.nutritional_info.calories.toFloat(),
                    protein = nutritionResponse.nutritional_info.totalNutrients.PROCNT.quantity.toFloat(),
                    carbs = nutritionResponse.nutritional_info.totalNutrients.CHOCDF.quantity.toFloat(),
                    fat = nutritionResponse.nutritional_info.totalNutrients.FAT.quantity.toFloat(),
                    allergens = extractAllergensFromRecognition(logMealResult!!)
                )
            } else {
                // Return zeros if no nutritional info available
                NutritionInfo(
                    calories = 0f,
                    protein = 0f,
                    carbs = 0f,
                    fat = 0f,
                    allergens = extractAllergensFromRecognition(logMealResult!!)
                )
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error getting nutrition info", e)
            return NutritionInfo(
                calories = 0f,
                protein = 0f,
                carbs = 0f,
                fat = 0f,
                allergens = extractAllergensFromRecognition(logMealResult!!)
            )
        }
    }

    private fun extractAllergensFromRecognition(response: LogMealResponse): List<String> {
        val potentialAllergens = mutableSetOf<String>()

        // Check food family
        response.foodFamily.forEach { family ->
            checkForAllergens(family.name, potentialAllergens)
        }

        // Check recognition results and subclasses
        response.recognition_results.forEach { result ->
            checkForAllergens(result.name, potentialAllergens)
            result.subclasses.forEach { subclass ->
                checkForAllergens(subclass.name, potentialAllergens)
            }
        }

        return potentialAllergens.toList()
    }

    private fun checkForAllergens(foodName: String, allergenSet: MutableSet<String>) {
        commonAllergens.forEach { allergen ->
            if (foodName.contains(allergen, ignoreCase = true)) {
                allergenSet.add(allergen)
            }
        }
    }

//    private fun analyzeFoodSafety(bitmap: Bitmap): SafetyInfo {
//        // Basic image analysis
//        return SafetyInfo(
//            isSafe = true, // Default to true for now
//            freshnessScore = 100, // Default score
//            spoilageDetails = listOf<String>("q","a")
//        )
//    }

    private fun createImageFile(bitmap: Bitmap): File {
        val file = File(context.cacheDir, "food_image.jpg")
        file.outputStream().use { out ->
            // Compress with higher quality and specific format
            bitmap.compress(Bitmap.CompressFormat.JPEG, 95, out)
        }

        // Verify file size
        if (file.length() > 5 * 1024 * 1024) { // If larger than 5MB
            val resizedBitmap = resizeImage(bitmap)
            file.outputStream().use { out ->
                resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 90, out)
            }
        }

        return file
    }

    private fun resizeImage(bitmap: Bitmap): Bitmap {
        val maxDimension = 1024
        val width = bitmap.width
        val height = bitmap.height

        if (width <= maxDimension && height <= maxDimension) {
            return bitmap
        }

        val ratio = width.toFloat() / height.toFloat()
        val newWidth: Int
        val newHeight: Int

        if (width > height) {
            newWidth = maxDimension
            newHeight = (maxDimension / ratio).toInt()
        } else {
            newHeight = maxDimension
            newWidth = (maxDimension * ratio).toInt()
        }

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
    }
}