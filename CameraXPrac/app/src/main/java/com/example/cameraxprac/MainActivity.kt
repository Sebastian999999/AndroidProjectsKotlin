package com.example.cameraxprac

import android.content.ContentResolver
import android.content.ContentValues
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresPermission
import androidx.appcompat.app.AppCompatActivity
import androidx.camera.core.CameraEffect
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.camera.view.CameraController
import androidx.camera.view.LifecycleCameraController
import androidx.concurrent.futures.await
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.lifecycleScope
import com.example.cameraxprac.databinding.ActivityMainBinding
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Locale
import java.util.concurrent.TimeUnit

class MainActivity : AppCompatActivity() {
    private lateinit var view:ActivityMainBinding
    private lateinit var cameraController: LifecycleCameraController
    private var imageCapture: ImageCapture? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        view = ActivityMainBinding.inflate(layoutInflater)
        setContentView(view.root)
        if(!hasPermissions(baseContext)){
            activityResultLauncher.launch(requiredPermissions)
        }else{
            //startCamera()
            lifecycleScope.launch{
                startCameraWithProvider()
            }
        }

        view.imageCaptureButton.setOnClickListener{
            //takePhoto()
            lifecycleScope.launch{
                takePhotoWithProvider()
            }
        }
    }
    private fun startCamera(){
        val previewView = view.viewFinder
        cameraController = LifecycleCameraController(baseContext)
        cameraController.bindToLifecycle(this)//As camera controller is lifecycle aware. s we need to give it lifecylce of the activity
        cameraController.cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA //For setting front camera. otherwise choses back camera by default if this is not set.
         previewView.controller = cameraController
    }

    private suspend fun startCameraWithProvider(){
        //Used await as get instance will return a listenable future which will contain the process provider function
        val cameraProvider = ProcessCameraProvider.getInstance(this).await()
        val preview = Preview.Builder().build()
        preview.surfaceProvider = view.viewFinder.surfaceProvider
        imageCapture = ImageCapture.Builder().build()
       val cameraSelector = CameraSelector.DEFAULT_FRONT_CAMERA
        try{
            cameraProvider.unbindAll()
            cameraProvider.bindToLifecycle(this,cameraSelector,preview,imageCapture)
        }
        catch(e:Exception){

        }
    }

    private suspend fun takePhotoWithProvider(){
        val name = SimpleDateFormat(FILENAME_FORMAT,Locale.US)
            .format(TimeUnit.MILLISECONDS)
        val contentValues = ContentValues().apply{
            put(MediaStore.MediaColumns.DISPLAY_NAME, name)
            put(MediaStore.MediaColumns.MIME_TYPE, "image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                put(MediaStore.Images.Media.RELATIVE_PATH,"picture/X-Camera-Provider")
            }
        }

        val outputOptions = ImageCapture.OutputFileOptions.Builder(
            contentResolver,
            MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        imageCapture?.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(output: ImageCapture.OutputFileResults) {
                    Toast.makeText(
                        this@MainActivity,
                        "Photo capture succeeded: ${output.savedUri}",
                        Toast.LENGTH_SHORT
                    ).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(
                        this@MainActivity,
                        "Photo capture failed: ${exception.message}",
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        )
    }
    private fun takePhoto(){
        //Create time stamped name and media store entry
        val name = SimpleDateFormat(FILENAME_FORMAT, Locale.US) //Defining name to use for photo file
            .format(System.currentTimeMillis())
        val contentValues = ContentValues().apply{
            put(MediaStore.MediaColumns.DISPLAY_NAME,name)
            put(MediaStore.MediaColumns.MIME_TYPE,"image/jpeg")
            if(Build.VERSION.SDK_INT > Build.VERSION_CODES.P){
                put(MediaStore.Images.Media.RELATIVE_PATH,"pictures/Camera-XPics")
            }
        }

        //creating output file options which contains file + metadata
        val outputOptions = ImageCapture.OutputFileOptions
            .Builder(
            contentResolver,
             MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
            contentValues
        ).build()

        cameraController.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(this),
            object: ImageCapture.OnImageSavedCallback{
                override fun onImageSaved(output:ImageCapture.OutputFileResults){
                    Toast.makeText(this@MainActivity, "Photo capture succeeded: ${output.savedUri}", Toast.LENGTH_SHORT).show()
                }

                override fun onError(exception: ImageCaptureException) {
                    Toast.makeText(this@MainActivity, "Photo capture failed: ${exception.message}", Toast.LENGTH_SHORT).show()
                }
            }
        )
    }
    private val activityResultLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()){permissions->
            var permissionsGranted = true
            permissions.entries.forEach{
                if(it.key in requiredPermissions && !it.value)
                    permissionsGranted = false
            }

            if(!permissionsGranted){
                Toast.makeText( this, "Permission request denied", Toast.LENGTH_SHORT).show()
            }
            else{
               //startCamera() //this is for comtrollers
                lifecycleScope.launch{
                    startCameraWithProvider()
                }
            }
        }
    companion object{
        private const val TAG = "CameraXApp" //Will be used for logging
        private const val FILENAME_FORMAT = "yyyy-MM-dd--HH--mm--ss--SSS" //Will be used for naming convention of files
        private val requiredPermissions = mutableListOf(
            android.Manifest.permission.CAMERA
        ).apply{
            if(Build.VERSION.SDK_INT <= Build.VERSION_CODES.P){
                add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            }
        }.toTypedArray()

        fun hasPermissions(context: Context) = requiredPermissions.all{
            ContextCompat.checkSelfPermission(context, it) == PackageManager.PERMISSION_GRANTED
        }
    }
}