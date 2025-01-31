package com.hammadirfan.smdproject
import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.ProgressBar
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.gms.tasks.Task
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.storage.FirebaseStorage
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.GET
import retrofit2.http.Query
import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.WRITE_EXTERNAL_STORAGE
import android.Manifest.permission.MANAGE_EXTERNAL_STORAGE
import android.Manifest.permission.POST_NOTIFICATIONS

import java.io.IOException
import java.net.URI
import java.net.URL


class AddProductFragment : Fragment() {

    interface NotificationService {
        @GET("send_notification.js")
        fun sendNotificationToUser(
            @Query("title") title: String,
            @Query("message") message: String
        ): Call<Void>
    }
    private val requestPermissionLauncher = registerForActivityResult(
        ActivityResultContracts.RequestPermission(),
    ) { isGranted: Boolean ->
        if (isGranted) {
            // FCM SDK (and your app) can post notifications.
        } else {
            // TODO: Inform user that that your app will not show notifications.
        }
    }
    private lateinit var FirebaseUtil : FirebaseUtil
    private lateinit var retrofit: Retrofit
    private lateinit var FirebaseMessage : FirebaseMessagingService
    private lateinit var notificationService: NotificationService
    private lateinit var productNameEditText: EditText
    private lateinit var productDescriptionEditText: EditText
    private lateinit var productPriceEditText: EditText
    private lateinit var productBarcodeEditText: EditText
    private lateinit var productQuantityEditText: EditText
    private lateinit var addButton: Button
    private var imageUri: Uri?= null
    private lateinit var productImageView: ImageView
    private lateinit var progressBar: ProgressBar
    private val imageRequestCode = 1  // Request code for image pick


    override fun onAttach(context: Context) {
        super.onAttach(context)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_add_product, container, false)
        initializeViews(view)



        addButton.setOnClickListener {
            uploadImageAndSaveProduct()
        }

        view.findViewById<Button>(R.id.buttonChooseImage).setOnClickListener {
            chooseImage()
        }

        return view
    }
    private fun uploadImageAndSaveProduct() {

        if (imageUri == null) {
            Toast.makeText(context, "Please select an image first.", Toast.LENGTH_SHORT).show()
            return
        }

        addButton.isEnabled = false
        progressBar.visibility = View.VISIBLE

        val fileName = "${System.currentTimeMillis()}.jpg"
        val storageRef = FirebaseStorage.getInstance().reference.child("images/$fileName")

        val uploadTask = context?.contentResolver?.openInputStream(imageUri!!)?.let { inputStream ->
            storageRef.putStream(inputStream)
        }

        uploadTask?.addOnSuccessListener {
           storageRef.downloadUrl.addOnSuccessListener { url ->
                Log.d("Firebase Storage URI", url.toString())
               imageUri = url
               Log.d("Local Storage updated URI after firebase cloud upload", imageUri.toString())
                saveProduct(url.toString())
            }
        }?.addOnFailureListener { exception ->
            Toast.makeText(context, "Failed to upload image: ${exception.message}", Toast.LENGTH_LONG).show()
        }?.addOnCompleteListener {
            addButton.isEnabled = true
            progressBar.visibility = View.GONE

        }

    }



    private fun saveProduct(imageUrl: String) {
        val name = productNameEditText.text.toString().trim()
        val description = productDescriptionEditText.text.toString().trim()
        val price = productPriceEditText.text.toString().toDoubleOrNull() ?: 0.0
        val barcode = productBarcodeEditText.text.toString().trim()
        val quantity = productQuantityEditText.text.toString().toIntOrNull() ?: 0
        if (name.isNotEmpty() && description.isNotEmpty() && price > 0 && barcode.isNotEmpty()) {
            val product = hashMapOf(
                "name" to name,
                "description" to description,
                "price" to price,
                "barcode" to barcode,
                "imageUrl" to imageUrl,
                "quantity" to quantity
            )
            // Get a reference to the Realtime Database
            val databaseReference = FirebaseDatabase.getInstance().getReference("products")
            // Create a new unique key for the product
            val key = databaseReference.push().key
            if (key != null) {
                databaseReference.child(key).setValue(product)
                    .addOnSuccessListener {
                        Toast.makeText(context, "Product added successfully", Toast.LENGTH_LONG).show()
                        addProduct()
                    }
                    .addOnFailureListener {
                        Toast.makeText(context, "Failed to add product: ${it.message}", Toast.LENGTH_LONG).show()
                    }
            } else {
                Toast.makeText(context, "Failed to create a unique key for the product", Toast.LENGTH_LONG).show()
            }
        } else {
            Toast.makeText(context, "Please fill in all fields", Toast.LENGTH_SHORT).show()
        }
    }


    private fun addProduct() {
        val name = productNameEditText.text.toString().trim()
        val description = productDescriptionEditText.text.toString().trim()
        val price = productPriceEditText.text.toString().trim()
        val barcode = productBarcodeEditText.text.toString().trim()
        val quantity = productQuantityEditText.text.toString().trim()
        //val imageUri = imageUri

        if (name.isNotEmpty() && description.isNotEmpty() && price.isNotEmpty() && barcode.isNotEmpty() && quantity.isNotEmpty() && imageUri != null) {


            RetrofitClient.instance.addProduct(name, description, price, barcode, imageUri.toString())
                .enqueue(object : retrofit2.Callback<ResponseBody> {
                    override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                        if (response.isSuccessful) {
                            Toast.makeText(context, "Product added successfully in mysql database !", Toast.LENGTH_SHORT).show()
                            sendNotificationToServer("New Product Added", "$name has been added to the database.")
                        } else {
                            Toast.makeText(context, "Failed to add product", Toast.LENGTH_SHORT).show()
                        }
                    }


                    override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                        Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_SHORT).show()
                    }
                })
        } else {
            Toast.makeText(context, "Please fill all fields and choose an image", Toast.LENGTH_SHORT).show()
        }
    }
    // Declare the launcher at the top of your Activity/Fragment:


    private fun askNotificationPermission() {
        // This is only necessary for API level >= 33 (TIRAMISU)
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
            if (context?.let { ContextCompat.checkSelfPermission(it, POST_NOTIFICATIONS) } ==
                PackageManager.PERMISSION_GRANTED
            ) {
                // FCM SDK (and your app) can post notifications.
            } else if (shouldShowRequestPermissionRationale(POST_NOTIFICATIONS)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.

            } else {
                // Directly ask for the permission
                requestPermissionLauncher.launch(POST_NOTIFICATIONS)
            }
        }
    }
    // Notification function using FCM
    private fun sendNotificationToServer(title: String, message: String) {
        retrofit = Retrofit.Builder()
            .baseUrl("http://172.17.67.216/myapp/")
            .addConverterFactory(GsonConverterFactory.create())
            .build()

        notificationService = retrofit.create(NotificationService::class.java)
        notificationService.sendNotificationToUser(title, message).enqueue(object : Callback<Void> {
            override fun onResponse(call: Call<Void>, response: Response<Void>) {
                if (response.isSuccessful) {
                    Toast.makeText(context, "Notification sent successfully", Toast.LENGTH_SHORT)
                        .show()
                    FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
                        if (!task.isSuccessful) {

                            return@OnCompleteListener
                        }
                        var token = task.result
                        // Create an Intent to broadcast to your MyFirebaseMessagingService
                        NotificationUtils.sendFCMMessage(context!!,token)
                        //NotificationUtils.sendNotification(context!!, "A new product has been added to inventory")
                        Log.i("My Token", token)
                })
                } else {
                    Toast.makeText(context, "Failed to send notification", Toast.LENGTH_SHORT).show()
                }
            }

            override fun onFailure(call: Call<Void>, t: Throwable) {
                Toast.makeText(context, "Error: ${t.message}", Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun initializeViews(view: View) {
        productNameEditText = view.findViewById(R.id.editTextProductName)
        productDescriptionEditText = view.findViewById(R.id.editTextProductDescription)
        productPriceEditText = view.findViewById(R.id.editTextProductPrice)
        productBarcodeEditText = view.findViewById(R.id.editTextProductBarcode)
        productQuantityEditText = view.findViewById(R.id.editTextProductQuantity)
        addButton = view.findViewById(R.id.buttonAddProduct)
        productImageView = view.findViewById(R.id.imageViewProduct)
        progressBar = view.findViewById(R.id.progressBar) // Initialize the ProgressBar
    }

    private fun chooseImage() {
        Log.d("ImagePicker", "Checking permissions for storage")
        if (ContextCompat.checkSelfPermission(requireContext(), android.Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            Log.d("ImagePicker", "Permission not granted, requesting permission")
            ActivityCompat.requestPermissions(requireActivity(), arrayOf(android.Manifest.permission.READ_EXTERNAL_STORAGE), imageRequestCode)
        } else {
            Log.d("ImagePicker", "Permission granted, opening image picker")
            val intent = Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI)
            startActivityForResult(intent, imageRequestCode)
        }
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<out String>, grantResults: IntArray) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        Log.d("ImagePicker", "Permission result received")
        if (requestCode == imageRequestCode && grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            Log.d("ImagePicker", "Permission granted, retrying image selection")
            chooseImage()
        } else {
            Log.d("ImagePicker", "Permission denied")
            Toast.makeText(context, "Permission denied to read your External storage", Toast.LENGTH_SHORT).show()
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (resultCode == Activity.RESULT_OK && requestCode == imageRequestCode) {
            data?.data?.let {
                imageUri = it  // Ensure this is set here
                productImageView.setImageURI(it)
            }
        }
    }



    override fun onDestroy() {
        super.onDestroy()
    }

}
