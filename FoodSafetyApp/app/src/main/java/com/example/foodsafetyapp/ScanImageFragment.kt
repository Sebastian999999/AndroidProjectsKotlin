package com.example.foodsafetyapp

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.annotation.OptIn
import androidx.camera.core.CameraSelector
import androidx.camera.core.ExperimentalGetImage
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.ImageProxy
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.PreviewView
import androidx.core.content.ContextCompat
import androidx.core.os.HandlerCompat.postDelayed
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.google.mlkit.vision.common.InputImage
import com.google.mlkit.vision.label.ImageLabeling
import com.google.mlkit.vision.label.defaults.ImageLabelerOptions
//import com.google.common.*
import com.google.mlkit.vision.objects.ObjectDetection
import com.google.mlkit.vision.objects.defaults.ObjectDetectorOptions
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class ScanImageFragment<ImageView : View?> : Fragment(R.layout.fragment_scan_image) {

    private lateinit var previewView: PreviewView
    private lateinit var cameraExecutor: ExecutorService
    private lateinit var boundingBoxOverlay: BoundingBoxOverlay
    private var imageAnalyzer: ImageAnalysis? = null
    private var imageCapture: ImageCapture? = null

    companion object {
        private const val TAG = "ScanImageFragment"
        private const val REQUEST_CODE_PERMISSIONS = 10
        private val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        previewView = view.findViewById(R.id.previewView)
        cameraExecutor = Executors.newSingleThreadExecutor()
        boundingBoxOverlay = view.findViewById(R.id.boundingBoxOverlay)

        // Set up capture button listener
        view.findViewById<View>(R.id.btnCapture).setOnClickListener {
            captureImage()
        }
        if (allPermissionsGranted()) {
            startCamera()
        } else {
            requestPermissions(REQUIRED_PERMISSIONS, REQUEST_CODE_PERMISSIONS)
        }
    }

    private fun captureImage() {
        val imageCapture = imageCapture ?: return
        imageCapture.takePicture(
            ContextCompat.getMainExecutor(requireContext()),
            object : ImageCapture.OnImageCapturedCallback() {
                override fun onCaptureSuccess(imageProxy: ImageProxy) {
                    // For demonstration, we can use the PreviewView's bitmap as a capture snapshot.
                    // In a real app, you might convert the imageProxy to a Bitmap properly.
                    val capturedBitmap: Bitmap? = previewView.bitmap

                    if (capturedBitmap != null) {
                        // Store the captured image in a shared variable (for demonstration)
                        FoodAnalysisFragment.capturedImage = capturedBitmap
                        Log.d(TAG, "Image captured successfully.")
                    } else {
                        Log.e(TAG, "Captured bitmap is null.")
                    }
                    imageProxy.close()
                    // Navigate back to FoodAnalysisFragment to display the captured image
                    findNavController().navigateUp()
                }

                override fun onError(exception: ImageCaptureException) {
                    Log.e(TAG, "Photo capture failed: ${exception.message}", exception)
                }
            }
        )
    }
    private fun startCamera() {
        val cameraProviderFuture = ProcessCameraProvider.getInstance(requireContext())//ProcessCameraProvider.getInstance(requireContext())

        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()

            // Build the Preview use case
            val preview = Preview.Builder()
                .build()
                .also { it.setSurfaceProvider(previewView.surfaceProvider) }

            // Build the ImageAnalysis use case for continuous detection
            imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also { analysis ->
                    analysis.setAnalyzer(cameraExecutor, ImageAnalysis.Analyzer { imageProxy ->
                        processImageProxy(imageProxy)
                    })
                }

            // Build the ImageCapture use case for taking a picture
            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    viewLifecycleOwner,
                    CameraSelector.DEFAULT_BACK_CAMERA,
                    preview,
                    imageAnalyzer,
                    imageCapture
                )
            } catch (exc: Exception) {
                Log.e(TAG, "Use case binding failed", exc)
            }
        }, ContextCompat.getMainExecutor(requireContext()))
    }

    // Update the processImageProxy function
    @OptIn(ExperimentalGetImage::class)
    private fun processImageProxy(imageProxy: ImageProxy) {
        val mediaImage = imageProxy.image
        if (mediaImage != null) {
            val rotationDegrees = imageProxy.imageInfo.rotationDegrees
            val inputImage = InputImage.fromMediaImage(mediaImage, rotationDegrees)

            // Configure food detection parameters
            val foodKeywords = listOf(
                "food", "fruit", "vegetable", "meal", "dish",
                "apple", "banana", "pizza", "burger", "sushi",
                "salad", "bread", "meat", "chicken", "fish",
                "rice", "pasta", "sandwich", "soup", "dessert"
            )
            val minConfidence = 0.5f

            val options = ObjectDetectorOptions.Builder()
                .setDetectorMode(ObjectDetectorOptions.STREAM_MODE)
                .enableMultipleObjects()
                .enableClassification()
                .build()

            val objectDetector = ObjectDetection.getClient(options)

            objectDetector.process(inputImage)
                .addOnSuccessListener { detectedObjects ->
                    val detections = mutableListOf<BoundingBoxOverlay.Detection>()

                    // Get dimensions
                    val imageWidth = mediaImage.width.toFloat()
                    val imageHeight = mediaImage.height.toFloat()
                    val viewWidth = previewView.width.toFloat()
                    val viewHeight = previewView.height.toFloat()

                    // Calculate scale factors
                    val scaleX = viewWidth / imageWidth
                    val scaleY = viewHeight / imageHeight
                    val scaleFactor = scaleX.coerceAtMost(scaleY)

                    // Calculate offsets for letterbox/pillarbox
                    val offsetX = (viewWidth - imageWidth * scaleFactor) / 2f
                    val offsetY = (viewHeight - imageHeight * scaleFactor) / 2f

                    for (detectedObject in detectedObjects) {
                        // Filter non-food items
                        val bestLabel = detectedObject.labels
                            .firstOrNull { it.confidence >= minConfidence }
                            ?.text
                            ?.lowercase()
                            ?: continue

                        if (!foodKeywords.any { bestLabel.contains(it) }) continue

                        // Convert bounding box coordinates
                        val bounds = detectedObject.boundingBox
                        val left = bounds.left * scaleFactor + offsetX
                        val top = bounds.top * scaleFactor + offsetY
                        val right = bounds.right * scaleFactor + offsetX
                        val bottom = bounds.bottom * scaleFactor + offsetY

                        // Create final rectangle with bounds checking
                        val finalRect = RectF(
                            left.coerceIn(0f, viewWidth),
                            top.coerceIn(0f, viewHeight),
                            right.coerceIn(0f, viewWidth),
                            bottom.coerceIn(0f, viewHeight)
                        )

                        detections.add(BoundingBoxOverlay.Detection(
                            bounds = finalRect,
                            label = detectedObject.labels.first().text,
                            confidence = detectedObject.labels.first().confidence
                        ))
                    }

                    boundingBoxOverlay.setDetections(detections)
                }
                .addOnFailureListener { e ->
                    Log.e(TAG, "Object detection failed", e)
                }
                 .addOnCompleteListener {
                    imageProxy.close()
                }
        } else {
            imageProxy.close()
        }
    }

    // Add this extension function
    private fun Float.coerceIn(min: Float, max: Float): Float {
        return if (this < min) min else if (this > max) max else this
    }


    private fun allPermissionsGranted() = REQUIRED_PERMISSIONS.all {
        ContextCompat.checkSelfPermission(requireContext(), it) ==
                PackageManager.PERMISSION_GRANTED
    }

    @Deprecated("Deprecated in Java")
    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        if (requestCode == REQUEST_CODE_PERMISSIONS) {
            if (allPermissionsGranted()) {
                startCamera()
            } else {
                Log.e(TAG, "Permissions not granted by the user.")
                requireActivity().finish()
            }
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        cameraExecutor.shutdown()
        imageAnalyzer?.clearAnalyzer()
    }
}
