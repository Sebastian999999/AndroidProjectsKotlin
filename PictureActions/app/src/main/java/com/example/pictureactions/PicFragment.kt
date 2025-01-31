package com.example.pictureactions

import android.graphics.Bitmap
import android.graphics.Matrix
import android.graphics.drawable.BitmapDrawable
import android.net.Uri
import android.os.Bundle
import android.provider.MediaStore
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.RadioGroup
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.android.material.button.MaterialButton
import com.google.android.material.radiobutton.MaterialRadioButton
import com.google.android.material.slider.Slider

class PicFragment : Fragment() {

    private lateinit var imageView: ImageView
    private lateinit var bottomnav: BottomNavigationView
    private lateinit var slider: Slider
    private var originalBitmap: Bitmap? = null // Store the original bitmap
    private var currentBitmap: Bitmap? = null // Store the current transformed bitmap
    private var currentRotationAngle = 0f // Track the current rotation angle

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view = inflater.inflate(R.layout.fragment_pic, container, false)
        imageView = view.findViewById(R.id.ivimage)
        bottomnav = view.findViewById(R.id.bottomnavview)
        slider = view.findViewById(R.id.slider)

        // Set up slider with -45 to 45 degrees
        slider.valueFrom = -60f
        slider.valueTo = 60f
        slider.value = 0f

        // Slider listener for rotation correction
        slider.addOnChangeListener { _, value, _ ->
            applyRotationCorrection(value.toInt()) // Now using integer input
        }

        imageView.setOnClickListener {
            imagepicker()
        }

        bottomnav.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.edit_icon -> {
                    showResolutionDialog()
                    true
                }
                R.id.mirror_icon -> {
                    mirrorImage(imageView)
                    true
                }
                R.id.rotate_icon -> true // The slider already handles rotation
                else -> false
            }
        }

        return view
    }

    private fun applyRotationCorrection(angle: Int) {
        originalBitmap?.let { bitmap ->
            // Create a new Matrix for transformations
            val matrix = Matrix()

            // Calculate the scale factors to fit the bitmap to the ImageView
            val scaleX = imageView.width / bitmap.width.toFloat()
            val scaleY = imageView.height / bitmap.height.toFloat()

            // Apply scaling to the matrix
            matrix.postScale(scaleX, scaleY)

            // Calculate pivot points based on the ImageView's dimensions
            val pivotX = imageView.width / 2f
            val pivotY = imageView.height / 2f

            // Apply rotation around the center of the ImageView
            matrix.postRotate(angle.toFloat(), pivotX, pivotY)

            // Set the ImageView scaleType to MATRIX to control transformation
            imageView.scaleType = ImageView.ScaleType.MATRIX

            // Apply the transformation matrix to the ImageView
            imageView.imageMatrix = matrix
        }
    }


    private val imagePickerLauncher = registerForActivityResult(
        ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            val bitmap = MediaStore.Images.Media.getBitmap(requireActivity().contentResolver, it)
            originalBitmap = bitmap // Store the original bitmap
            currentBitmap = bitmap // Set the current bitmap to the original one

            // Set the ImageView scale type to FIT_XY to resize the image to fit the ImageView
            imageView.scaleType = ImageView.ScaleType.FIT_XY
            imageView.setImageDrawable(BitmapDrawable(resources, bitmap)) // Set the image
        }
    }



    private fun imagepicker() {
        imagePickerLauncher.launch("image/*")
    }

    private fun mirrorImage(imageView: ImageView) {
        val drawable = imageView.drawable ?: return
        val bitmap = (drawable as BitmapDrawable).bitmap

        val matrix = Matrix()
        matrix.preScale(-1.0f, 1.0f)

        val mirroredBitmap = Bitmap.createBitmap(
            bitmap, 0, 0, bitmap.width, bitmap.height, matrix, false
        )
        imageView.setImageDrawable(BitmapDrawable(resources, mirroredBitmap))
    }

    private fun showResolutionDialog() {
        val dialogView = layoutInflater.inflate(R.layout.dialog_card, null)

        val dialog = android.app.AlertDialog.Builder(requireContext())
            .setView(dialogView)
            .setCancelable(true)
            .create()

        dialog.window?.setBackgroundDrawableResource(android.R.color.transparent) // Transparent background
        dialog.show()

        // Find views in the dialog
        val radioGroup = dialogView.findViewById<RadioGroup>(R.id.rgpixels)
        val btnOk = dialogView.findViewById<MaterialButton>(R.id.mbok)

        // Change color on selection
        radioGroup.setOnCheckedChangeListener { group, checkedId ->
            for (i in 0 until group.childCount) {
                val radioButton = group.getChildAt(i) as MaterialRadioButton
                if (radioButton.id == checkedId) {
                    radioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.blue_primary_color))
                } else {
                    radioButton.setTextColor(ContextCompat.getColor(requireContext(), R.color.black))
                }
            }
        }

        btnOk.setOnClickListener {
            val selectedId = radioGroup.checkedRadioButtonId
            if (selectedId != -1) {
                val selectedButton = dialogView.findViewById<MaterialRadioButton>(selectedId)
                val selectedResolution = selectedButton.text.toString()
                Toast.makeText(requireContext(), "Selected: $selectedResolution", Toast.LENGTH_SHORT).show()
            }
            dialog.dismiss()
        }
    }


}