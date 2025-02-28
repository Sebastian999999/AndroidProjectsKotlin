package com.example.foodsafetyapp.models
import android.util.Log
import org.opencv.core.Point
import android.widget.Toast
import androidx.core.content.ContentProviderCompat.requireContext
//import nu.pattern.OpenCV  // instead of org.opencv.android.OpenCVLoader
import androidx.room.util.splitToIntList
import com.google.android.datatransport.runtime.util.PriorityMapping.toInt
import org.opencv.android.Utils.bitmapToMat
import android.graphics.Bitmap as Bitmap1
import org.opencv.utils.Converters.vector_int_to_Mat
import org.opencv.core.Core
import org.opencv.core.MatOfInt
import org.opencv.core.MatOfFloat
import org.opencv.core.Mat
import org.opencv.core.CvType
import org.opencv.core.MatOfDouble
import org.opencv.core.MatOfPoint
import org.opencv.core.MatOfPoint2f
import org.opencv.core.Scalar
import org.opencv.core.Size
import org.opencv.imgproc.Imgproc
import kotlin.math.ln
import kotlin.math.min
import kotlin.math.pow
import kotlin.math.sqrt

class SpoilageDetector {
    init {
        try {
            // Ensure OpenCV is loaded in this class too
            System.loadLibrary("opencv_java4")
        } catch (e: Exception) {
            Log.e("OpenCV", "Error loading OpenCV in SpoilageDetector: ${e.message}")
        }
    }
    data class SpoilageResult(
        val isSpoiled: Boolean,
        val confidence: Float,
        val details: List<String>,
        val recommendedAction: String
    )

    private data class MoldColorRange(
        val lower: Scalar,
        val upper: Scalar,
        val weight: Float
    )

    private fun detectMold(mat: Mat): Float {
        val moldColorRanges = listOf(
            MoldColorRange(
                lower = Scalar(25.0, 50.0, 30.0),  // Expanded green range
                upper = Scalar(95.0, 255.0, 220.0),
                weight = 0.6f
            ),
            MoldColorRange(
                lower = Scalar(0.0, 0.0, 50.0),     // Lowered value for white/gray
                upper = Scalar(180.0, 60.0, 200.0),
                weight = 0.4f
            ),
            MoldColorRange(
                lower = Scalar(0.0, 50.0, 0.0),     // Dark spots with some saturation
                upper = Scalar(180.0, 255.0, 80.0),
                weight = 0.4f
            )
        )

        var totalScore = 0f
        val hsv = Mat()
        try {
            // Convert to HSV with better color preservation
            Imgproc.cvtColor(mat, hsv, Imgproc.COLOR_RGB2HSV_FULL)

            moldColorRanges.forEach { range ->
                val mask = Mat()
                try {
                    Core.inRange(hsv, range.lower, range.upper, mask)
                    processMask(mask)
                    totalScore += calculateMoldScore(mask, hsv.size()) * range.weight
                } finally {
                    mask.release()
                }
            }
        } finally {
            hsv.release()
        }

        return totalScore.coerceIn(0f, 1f)
    }

    private fun processMask(mask: Mat) {
        val kernelSize = when {
            mask.cols() > 1000 -> 9.0
            mask.cols() > 500 -> 7.0
            else -> 5.0
        }

        val kernel = Imgproc.getStructuringElement(
            Imgproc.MORPH_ELLIPSE,
            Size(kernelSize, kernelSize)
        )

        // Create default anchor point
        val anchor = Point(-1.0, -1.0)

        // Pass anchor explicitly instead of null
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel, anchor, 2)
        Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_OPEN, kernel, anchor, 1)
    }

    private fun calculateMoldScore(mask: Mat, size: Size): Float {
        val contours = mutableListOf<MatOfPoint>()
        Imgproc.findContours(
            mask, contours, Mat(),
            Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE
        )

        val minArea = size.area() * 0.003 // 0.3% of image area
        var moldArea = 0.0

        contours.forEach { contour ->
            if (contour.rows() < 5) return@forEach // Skip small contours

            val area = Imgproc.contourArea(contour)
            if (area > minArea) {
                val perimeter = Imgproc.arcLength(MatOfPoint2f(*contour.toArray()), true)
                val compactness = 4 * Math.PI * area / (perimeter * perimeter)

                if (compactness > 0.18) { // More lenient compactness threshold
                    moldArea += area
                }
            }
        }

        return (moldArea / size.area()).toFloat()
    }

//    private fun detectMold(mat: Mat): Float {
//        val moldColors = listOf(
//            // Green-blue bread mold (Penicillium)
//            Triple(
//                Scalar(80.0, 40.0, 40.0),   // Lower HSV bound
//                Scalar(110.0, 255.0, 255.0), // Upper HSV bound
//                0.4f                         // Weight
//            ),
//            // Green bread mold
//            Triple(
//                Scalar(35.0, 40.0, 40.0),    // Lower HSV
//                Scalar(85.0, 255.0, 255.0),  // Upper HSV
//                0.4f                         // Weight
//            ),
//            // White/Gray mold (early stage)
//            Triple(
//                Scalar(0.0, 0.0, 180.0),     // Lower HSV
//                Scalar(180.0, 30.0, 255.0),  // Upper HSV
//                0.2f                         // Weight
//            )
//        )
//
//        var moldScore = 0.0
//        moldColors.forEach { (lower, upper, weight) ->
//            val mask = Mat()
//            try {
//                // Create binary mask for this color range
//                Core.inRange(mat, lower, upper, mask)
//
//                // Reduce noise and connect nearby regions
//                val kernel = Imgproc.getStructuringElement(
//                    Imgproc.MORPH_ELLIPSE,
//                    Size(3.0, 3.0)
//                )
//                Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel)
//
//                // Calculate percentage of affected area
//                val moldPixels = Core.countNonZero(mask)
//                val coverage = moldPixels.toDouble() / (mat.rows() * mat.cols())
//
//                // Add weighted contribution if significant coverage detected
//                if (coverage > 0.001) { // Lower threshold to catch smaller spots
//                    moldScore += coverage * weight
//                }
//            } finally {
//                mask.release()
//            }
//        }
//
//        return moldScore.toFloat().coerceIn(0f, 1f)
//    }

    // Update detection thresholds in detectSpoilage
    fun detectSpoilage(bitmap: Bitmap1): SpoilageResult {
        val mat = Mat().apply { bitmapToMat(bitmap, this) }
        Imgproc.cvtColor(mat, mat, Imgproc.COLOR_RGB2HSV)

        try {
            val moldScore = detectMold(mat)
            val rotScore = detectRot(mat)
            val textureScore = 0f//detectAbnormalTexture(mat)

            // Lower threshold for positive mold detection
            val isSpoiled = moldScore > 0.05f || rotScore > 0.3f ||
                    (moldScore + rotScore + textureScore) / 3 > 0.2f

            val confidence = (moldScore * 0.6f + rotScore * 0.3f + textureScore * 0.1f)
                .coerceIn(0f, 1f)

            return SpoilageResult(
                isSpoiled = isSpoiled,
                confidence = confidence,
                details = buildIssueList(moldScore, rotScore, textureScore),
                recommendedAction = when {
                    moldScore > 0.05f -> "DANGER: Significant mold detected - Do not consume!"
                    rotScore > 0.3f -> "WARNING: Signs of spoilage detected - Discard immediately"
                    isSpoiled -> "Caution: Some signs of spoilage detected"
                    else -> "Food appears normal"
                }
            )
        } finally {
            mat.release()
        }
    }

    private fun buildIssueList(
        moldScore: Float,
        rotScore: Float,
        textureScore: Float
    ): List<String> {
        val issues = mutableListOf<String>()

        Log.d("SCOOOOOOORE",moldScore.toString())
        if (moldScore > 0.05f) {
            issues.add("SEVERE: Visible mold growth detected")
        }
        if (rotScore > 0.3f) {
            issues.add("WARNING: Signs of decomposition present")
        }
        if (textureScore > 0.4f) {
            issues.add("Abnormal texture detected")
        }

        return if (issues.isEmpty()) {
            listOf("No obvious signs of spoilage")
        } else {
            issues + "Food safety concern: Do not consume"
        }
    }

    private fun generateRecommendation(moldScore: Float, discolorationScore: Float): String {
        return when {
            moldScore > 0.3f -> "Immediate disposal recommended - high mold risk"
            moldScore > 0.2f -> "Do not consume - possible mold presence"
            discolorationScore > 0.8f -> "Discard if unpleasant odor present"
            discolorationScore > 0.6f -> "Inspect carefully before consumption"
            else -> "Food appears safe for consumption"
        }
    }


    private fun detectDiscoloration(mat: Mat): Float {
        // Split channels to analyze both color and intensity
        val channels = ArrayList<Mat>()
        Core.split(mat, channels)

        // Calculate histograms for both Hue and Value channels
        val hueHist = calculateChannelHistogram(channels[0], 30, 0f, 180f)
        val valHist = calculateChannelHistogram(channels[2], 30, 0f, 255f)

        // Look for dark regions and color abnormalities
        val darkRegionScore = calculateDarkRegionScore(valHist)
        val colorVariationScore = calculateColorVariationScore(hueHist)

        return maxOf(darkRegionScore, colorVariationScore)
    }

    private fun calculateChannelHistogram(channel: Mat, bins: Int, rangeStart: Float, rangeEnd: Float): FloatArray {
        val hist = Mat()
        Imgproc.calcHist(
            listOf(channel),
            MatOfInt(0),
            Mat(),
            hist,
            MatOfInt(bins),
            MatOfFloat(rangeStart, rangeEnd)
        )
        val histData = FloatArray(bins)
        hist.get(0, 0, histData)
        return histData
    }

    private fun calculateDarkRegionScore(valueHist: FloatArray): Float {
        // Focus on very dark regions (first 10% instead of 20%)
        val darkRegionSum = valueHist.take(3).sum()  // Reduced from 6 to 3 bins
        val totalSum = valueHist.sum()
        return minOf(1.0f, (darkRegionSum / totalSum) * 2.0f)  // Reduced multiplier
    }

    private fun calculateColorVariationScore(hueHist: FloatArray): Float {
        val mean = hueHist.average()
        // Add small epsilon to prevent division by zero
        val eps = 0.001
        val stdDev = sqrt(hueHist.map { (it - mean).pow(2) }.average()) + eps
        return minOf(1.0f, (stdDev / (mean + eps)).toFloat() * 1.0f)  // Reduced multiplier
    }

    private fun detectAbnormalTexture(mat: Mat): Float {
        val gray = Mat()
        try {
            Imgproc.cvtColor(mat, gray, Imgproc.COLOR_HSV2BGR)
            Imgproc.cvtColor(gray, gray, Imgproc.COLOR_BGR2GRAY)

            // Multi-scale variance analysis
            val scales = listOf(1, 2, 4)
            val variances = scales.map { scale ->
                val resized = Mat()
                Imgproc.resize(gray, resized, Size(gray.cols()/scale.toDouble(), gray.rows()/scale.toDouble()))
                calculateTextureVariance(resized)
            }

            // Focus on relative variance changes across scales
            return variances.maxOrNull()!! * 0.7f +
                    (variances.maxOrNull()!! - variances.minOrNull()!!) * 0.3f
        } finally {
            gray.release()
        }
    }

    private fun calculateTextureVariance(img: Mat): Float {
        val mean = MatOfDouble()
        val stdDev = MatOfDouble()
        Core.meanStdDev(img, mean, stdDev)
        return stdDev.toArray()[0].toFloat() / 255f
    }

    // Adaptive threshold improvements
    private fun calculateAdaptiveThresholds(mat: Mat): Triple<Float, Float, Float> {
        val channels = ArrayList<Mat>(3).apply { Core.split(mat, this) }
        try {
            val vChannel = channels[2]
            val hChannel = channels[0]

            // Calculate image brightness
            val brightness = Core.mean(vChannel).`val`[0] / 255.0

            // Calculate color consistency
            val colorSpread = Core.mean(hChannel).`val`[0].let { meanHue ->
                val diff = Mat()
                Core.absdiff(hChannel, Scalar(meanHue), diff)
                Core.mean(diff).`val`[0] / 180.0
            }

            return Triple(
                // Mold: Base 0.2 + brightness adjustment
                (0.15f + (brightness * 0.2f).toFloat()).coerceIn(0.15f, 0.35f),
                // Discoloration: Base 0.6 adjusted by color diversity
                (0.5f + (colorSpread * 0.4f).toFloat()).coerceIn(0.5f, 0.9f),
                // Texture: Base 0.5, adjusted by image contrast
                (0.4f + (brightness * 0.4f).toFloat()).coerceIn(0.4f, 0.8f)
            )
        } finally {
            channels.forEach { it.release() }
        }
    }

    // ... helper methods for contour validation and resource management ...

    private fun hasSignificantContours(mask: Mat, minArea: Double): Boolean {
        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(mask, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)
        return contours.any { Imgproc.contourArea(it) > minArea }
    }

    private fun calculateTextureAtScale(gray: Mat, scale: Int): Float {
        val resized = Mat()
        Imgproc.resize(gray, resized, Size(gray.cols()/scale.toDouble(), gray.rows()/scale.toDouble()))
        val glcm = calculateGLCM(resized)
        return calculateTextureScore(glcm)
    }

    // Add class-level helper function first
    private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t.coerceIn(0f, 1f)

//    private fun calculateAdaptiveThresholds(mat: Mat): Triple<Float, Float, Float> {
//        val channels = ArrayList<Mat>(3)
//        Core.split(mat, channels)  // Split into 0:H, 1:S, 2:V
//
//        return try {
//            // Get Value channel (brightness)
//            val meanValue = Core.mean(channels[2]).val[0] / 255.0f
//
//            // Get Hue spread
//            val colorSpread = calculateColorSpread(channels[0])
//
//            Triple(
//                0.2f,  // Fixed mold threshold
//                lerp(0.5f, 0.7f, meanValue.toFloat()),  // Discoloration threshold
//                lerp(0.4f, 0.6f, colorSpread) // Texture threshold
//            )
//        } finally {
//            // Release channel Mats
//            channels.forEach { it.release() }
//        }
//    }

    private fun calculateColorSpread(hueChannel: Mat): Float {
        val converted = Mat()
        val mean = MatOfDouble()
        val stdDev = MatOfDouble()

        return try {
            // Convert to 64-bit floating point for calculation
            hueChannel.convertTo(converted, CvType.CV_64F)

            // Calculate statistics
            Core.meanStdDev(converted, mean, stdDev)

            // Extract standard deviation value
            val stdVal = stdDev.toArray()[0]  // Returns DoubleArray

            // Normalize to 0.0-1.0 range (max hue is 180° in OpenCV)
            (stdVal / 180.0).toFloat().coerceIn(0f, 1f)
        } catch (e: Exception) {
            Log.e("SpoilageDetector", "Error calculating color spread: ${e.message}")
            0f
        } finally {
            // Release all Mats to prevent memory leaks
            arrayOf(converted, mean, stdDev).forEach { it.release() }
        }
    }

    private fun detectRot(mat: Mat): Float {
        val rotRanges = listOf(
            // Black rot (common in fruits, vegetables, bread)
            RotRange(
                Scalar(0.0, 0.0, 0.0),     // Deep black
                Scalar(180.0, 255.0, 50.0),
                0.4f
            ),
            // Brown rot (fruits, vegetables, meats)
            RotRange(
                Scalar(10.0, 50.0, 20.0),   // Brown spectrum
                Scalar(25.0, 255.0, 150.0),
                0.3f
            ),
            // Soft rot/bacterial rot (vegetables, especially potatoes, carrots)
            RotRange(
                Scalar(20.0, 30.0, 30.0),   // Dark brown to black
                Scalar(40.0, 255.0, 150.0),
                0.3f
            ),
            // Gray rot (Botrytis - common in fruits, vegetables)
            RotRange(
                Scalar(0.0, 0.0, 120.0),    // Gray spectrum
                Scalar(180.0, 30.0, 200.0),
                0.3f
            ),
            // Blue rot (Penicillium - fruits)
            RotRange(
                Scalar(85.0, 50.0, 50.0),   // Blue-gray
                Scalar(130.0, 255.0, 255.0),
                0.3f
            ),
            // Pink rot (potatoes, root vegetables)
            RotRange(
                Scalar(150.0, 30.0, 50.0),  // Pink to rose
                Scalar(180.0, 255.0, 255.0),
                0.25f
            ),
            // Anthracnose (dark, sunken lesions)
            RotRange(
                Scalar(0.0, 0.0, 20.0),     // Very dark spots
                Scalar(180.0, 255.0, 80.0),
                0.35f
            ),
            // Sour rot (citrus fruits, grapes)
            RotRange(
                Scalar(15.0, 30.0, 50.0),   // Brown-yellow
                Scalar(35.0, 255.0, 200.0),
                0.25f
            ),
            // Bacterial soft rot (vegetables)
            RotRange(
                Scalar(40.0, 20.0, 30.0),   // Dark water-soaked areas
                Scalar(80.0, 150.0, 150.0),
                0.3f
            ),
            // General discoloration/decomposition
            RotRange(
                Scalar(0.0, 30.0, 30.0),    // Catch-all for unusual colors
                Scalar(180.0, 150.0, 180.0),
                0.2f
            )
        )

        var rotScore = 0.0
        rotRanges.forEach { range ->
            val mask = Mat()
            try {
                Core.inRange(mat, range.lower, range.upper, mask)

                // Enhanced morphological operations for better region detection
                val kernel = Imgproc.getStructuringElement(
                    Imgproc.MORPH_ELLIPSE,
                    Size(7.0, 7.0)
                )
                Imgproc.morphologyEx(mask, mask, Imgproc.MORPH_CLOSE, kernel)

                // Calculate affected areas
                val pixels = Core.countNonZero(mask)
                val coverage = pixels.toDouble() / (mat.rows() * mat.cols())

                // Consider significant areas only
                if (coverage > 0.02) {  // 2% threshold for significance
                    rotScore += coverage * range.maxContribution
                }
            } finally {
                mask.release()
            }
        }

        // Additional weighting based on clustered rot detection
        return (rotScore * 1.2f).toFloat().coerceIn(0f, 1f)  // Slight boost to rot detection sensitivity
    }

    private fun calculateSignificantRotScore(mask: Mat, maxContribution: Float): Float {
        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(mask, contours, Mat(), Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        // Convert the computation to double for sumOf and finally convert the result back to float if needed.
        val totalArea = contours.sumOf { contour ->
            val area = Imgproc.contourArea(contour).toDouble()  // Convert area to Double
            if (area > mask.total() * 0.002) {  // Use Double and adjust condition
                min(area / mask.total(), maxContribution.toDouble())  // maxContribution to Double as well
            } else {
                0.0  // Return Double type 0
            }
        }

        return totalArea.toFloat()  // Convert the result back to Float
    }

    private fun calculateRotScore(mask: Mat, maxContribution: Float, imageSize: Size): Float {
        val contours = ArrayList<MatOfPoint>()
        Imgproc.findContours(mask, contours, Mat(),
            Imgproc.RETR_EXTERNAL, Imgproc.CHAIN_APPROX_SIMPLE)

        // Require minimum 0.2% of image area and compact shape
        return contours.sumOf {
            val area = Imgproc.contourArea(it)
            val perimeter = Imgproc.arcLength(MatOfPoint2f(*it.toArray()), true)

            // For Kotlin/JS compatibility, use kotlin.math.PI
            val compactness = if (perimeter > 0) 4.0 * kotlin.math.PI * area / (perimeter * perimeter) else 0.0

            if (area > imageSize.area() * 0.002 && compactness > 0.3) {
                (area / imageSize.area().toDouble()) // Keep as Double for sum
                    .coerceAtMost(maxContribution.toDouble())
            } else 0.0
        }.toFloat() // Convert final sum to Float
    }

    // private fun lerp(a: Float, b: Float, t: Float) = a + (b - a) * t
    private fun calculateDiscolorationScore(histogram: FloatArray): Float {
        // Calculate mean and standard deviation of histogram
        val mean = histogram.average().toFloat()
        val stdDev = sqrt(histogram.map { (it - mean).pow(2) }.average()).toFloat()

        // Calculate histogram peaks and valleys
        val peaks = histogram.filterIndexed { index, value ->
            if (index == 0 || index == histogram.size - 1) false
            else value > histogram[index - 1] && value > histogram[index + 1]
        }

        // Calculate histogram uniformity
        val histSum = histogram.sum()
        val uniformity = histogram.fold(0f) { acc, value ->
            acc + (value / histSum).pow(2)
        }

        // Calculate entropy
        val entropy = histogram.fold(0f) { acc, value ->
            val prob = value / histSum
            if (prob > 0)
                acc - (prob * log2(prob))
            else
                acc
        }

        // Combine metrics for final score
        val peakScore = peaks.size.toFloat() / histogram.size
        val stdDevScore = stdDev / mean
        val entropyScore = entropy / log2(histogram.size.toFloat())

        return (peakScore + stdDevScore + (1 - uniformity) + entropyScore) / 4
    }

    private fun calculateGLCM(gray: Mat): Mat {
        // Quantize the image to reduce computation
        val levels = 8
        val scaled = Mat()
        Core.normalize(gray, scaled, 0.0, levels - 1.0, Core.NORM_MINMAX)
        scaled.convertTo(scaled, CvType.CV_8U)

        // Initialize GLCM matrix
        val glcm = Mat.zeros(levels, levels, CvType.CV_32F)

        // Calculate GLCM for horizontal direction (0 degrees)
        val rows = scaled.rows()
        val cols = scaled.cols()

        for (i in 0 until rows) {
            for (j in 0 until cols - 1) {
                val i_val = scaled.get(i, j)[0].toInt()
                val j_val = scaled.get(i, j + 1)[0].toInt()
                glcm.put(i_val, j_val, glcm.get(i_val, j_val)[0] + 1)
            }
        }

        // Symmetrize the matrix
        val glcmT = Mat()
        Core.transpose(glcm, glcmT)
        Core.add(glcm, glcmT, glcm)

        // Normalize the GLCM
        Core.normalize(glcm, glcm, 0.0, 1.0, Core.NORM_MINMAX)

        return glcm
    }

    private fun calculateTextureScore(glcm: Mat): Float {
        val contrast = calculateContrast(glcm)
        val homogeneity = calculateHomogeneity(glcm)
        val energy = calculateEnergy(glcm)
        val correlation = calculateCorrelation(glcm)

        // Combine texture features
        return (contrast + (1 - homogeneity) + (1 - energy) + (1 - correlation)) / 4
    }

    private fun calculateContrast(glcm: Mat): Float {
        var contrast = 0.0
        val size = glcm.rows()

        for (i in 0 until size) {
            for (j in 0 until size) {
                contrast += glcm.get(i, j)[0] * ((i - j) * (i - j))
            }
        }

        return contrast.toFloat()
    }

    private fun calculateHomogeneity(glcm: Mat): Float {
        var homogeneity = 0.0
        val size = glcm.rows()

        for (i in 0 until size) {
            for (j in 0 until size) {
                homogeneity += glcm.get(i, j)[0] / (1 + ((i - j) * (i - j)))
            }
        }

        return homogeneity.toFloat()
    }

    private fun calculateEnergy(glcm: Mat): Float {
        var energy = 0.0
        val size = glcm.rows()

        for (i in 0 until size) {
            for (j in 0 until size) {
                energy += glcm.get(i, j)[0].pow(2)
            }
        }

        return energy.toFloat()
    }

    private fun calculateCorrelation(glcm: Mat): Float {
        val size = glcm.rows()
        var mean = 0.0
        var stdDev = 0.0

        // Calculate mean
        for (i in 0 until size) {
            for (j in 0 until size) {
                mean += i * glcm.get(i, j)[0]
            }
        }

        // Calculate standard deviation
        for (i in 0 until size) {
            for (j in 0 until size) {
                stdDev += (i - mean).pow(2) * glcm.get(i, j)[0]
            }
        }
        stdDev = sqrt(stdDev)

        // Calculate correlation
        var correlation = 0.0
        for (i in 0 until size) {
            for (j in 0 until size) {
                correlation += (i - mean) * (j - mean) * glcm.get(i, j)[0] / (stdDev * stdDev)
            }
        }

        return correlation.toFloat()
    }

    private  class RotRange(
        val lower: Scalar,
        val upper: Scalar,
        val maxContribution: Float
    )
    private fun log2(x: Float): Float = ln(x.toDouble()).toFloat() / ln(2.0).toFloat()
}