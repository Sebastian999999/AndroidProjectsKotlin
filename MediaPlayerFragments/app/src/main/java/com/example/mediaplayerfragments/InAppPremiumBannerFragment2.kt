package com.example.mediaplayerfragments

import android.app.Dialog
import android.graphics.Color
import android.graphics.Shader
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.InsetDrawable
import android.graphics.drawable.LayerDrawable
import android.os.Bundle
import android.util.Log
import android.util.TypedValue
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.graphics.drawable.toDrawable
import androidx.core.view.marginBottom
import androidx.core.widget.addTextChangedListener
import com.example.mediaplayerfragments.R.*
import com.example.mediaplayerfragments.databinding.FragmentInAppPremiumBanner2Binding
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.android.material.dialog.MaterialDialogs.insetDrawable
import com.google.android.play.core.review.ReviewException
import com.google.android.play.core.review.ReviewManagerFactory
import com.google.android.play.core.review.model.ReviewErrorCode

//import com.google.android.material.internal.ViewUtils.dpToPx


class InAppPremiumBannerFragment2 : Fragment() {

    private lateinit var viewFrag: FragmentInAppPremiumBanner2Binding
    private var botView : BottomSheetDialog? = null
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        viewFrag = FragmentInAppPremiumBanner2Binding.inflate(inflater, container, false)
        viewFrag.mbfileaccess.setOnClickListener{

            botView = showBottomsheet()

                showFeedbackDialog(botView!!)

            //val ratingbar = dialogView.findViewById<RatingBar>(R.id.rating)
            //ratingbar.numStars = 5
            //ratingbar.progressDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.rating_star_filled)
//            val ratingBar = dialogView.findViewById<RatingBar>(R.id.rating)
//            // Convert 5dp to pixels
//            val insetPx = dpToPx(5)
//            // Retrieve and modify the progress drawable
//            val drawable = ratingBar.progressDrawable
//            ratingBar.progressDrawable = tileify(drawable, insetPx)
//            val progressDrawable = ContextCompat.getDrawable(requireContext(), R.drawable.starfilled)!!
//            val backgroundDrawable = ContextCompat.getDrawable(requireContext(),R.drawable.starunfilled)!!
//// Wrap it in an InsetDrawable with left/right insets of 5 pixels (or convert dp to pixels if needed)
//            val insetProgressDrawable = InsetDrawable(progressDrawable, 5, 0, 5, 0)
//            val insetBackgroundDrawable = InsetDrawable(backgroundDrawable,5,0,5,0)
//// Now assign the inset drawable to your RatingBar's progressDrawable
//            ratingbar.progressDrawable = insetProgressDrawable
            //ratingbar.background = insetBackgroundDrawable

        }
        if (botView!=null){
            showFeedbackDialog(botView!!)
        }
        return viewFrag.root//inflater.inflate(R.layout.fragment_in_app_premium_banner2, container, false)
    }

    private fun showBottomsheet() : BottomSheetDialog {
        val dialogView = layoutInflater.inflate(layout.bottom_sheet_rating,null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogView)
        dialog.show()
        return dialog
    }

    private fun showFeedbackDialog(dialog: BottomSheetDialog){
        val dialogView = dialog
        val rating = dialogView.findViewById<RatingBar>(R.id.rating)
        dialogView.findViewById<Button>(R.id.mbrate)?.setOnClickListener{
            if (rating?.rating!! <= 3.0f){
                dialogView.dismiss()
                negativeFeedbackBottomsheet()
            }
            else{
                positiveFeedbackPlaystoreSheet()
            }
        }

    }

    private fun positiveFeedbackPlaystoreSheet(){
        val manager = ReviewManagerFactory.create(requireContext().applicationContext)
        val request = manager.requestReviewFlow()
        //Toast.makeText(requireContext(), "OIIIIIIIIIIIIII", Toast.LENGTH_SHORT).show()
        // We got the ReviewInfo object
        request.addOnCompleteListener { task ->
            if (task.isSuccessful) {
                Toast.makeText(requireContext(), "Review flow successful", Toast.LENGTH_SHORT).show()
                val reviewInfo = task.result
                val flow = manager.launchReviewFlow(requireActivity(), reviewInfo)
                flow.addOnCompleteListener { _ ->
                    Toast.makeText(requireContext(), "Review flow completed", Toast.LENGTH_SHORT).show()
                }
            } else {
                Toast.makeText(requireContext(), "Review flow failed: ${task.exception?.message}", Toast.LENGTH_LONG).show()
                val reviewErrorCode = (task.exception as? ReviewException)?.errorCode
                Log.e("ReviewAPI", "Error code: $reviewErrorCode")
            }
        }
    }
    private fun negativeFeedbackBottomsheet(){
        val dialogView = layoutInflater.inflate(R.layout.bottomsheet_feedback_negative,null)
        val dialog = BottomSheetDialog(requireContext())
        dialog.setContentView(dialogView)
        dialog.show()
        val thanksTxt = dialogView.findViewById<TextView>(R.id.mtvratingsubmissionthanks)
        val feedbackbtn = dialogView.findViewById<Button>(R.id.mbsubmitfeedback)
        val closebtn = dialogView.findViewById<Button>(R.id.mbclose)
        val imageView = dialogView.findViewById<ImageView>(R.id.mivdelightfulemoji)
        val metfeedbacknegative = dialogView.findViewById<EditText>(R.id.metfeedbacknegative)
        val constraintLayout = dialogView.findViewById<ConstraintLayout>(R.id.clnegativefeedbacksheet)

        metfeedbacknegative.addTextChangedListener(
            onTextChanged = { text, start , end , count ->
                if (text!!.isNotEmpty()){
                    feedbackbtn.isEnabled = true
                    feedbackbtn.setTextColor(resources.getColor(R.color.white))
                    feedbackbtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.color_secondary_blue)

                    feedbackbtn.setOnClickListener{
                        imageView.visibility = View.VISIBLE
                        closebtn.visibility = View.VISIBLE
                        feedbackbtn.visibility = View.GONE
                        metfeedbacknegative.visibility = View.GONE
                        val constraintset = ConstraintSet()

                        Toast.makeText(requireContext(),"HMMMMMMMMM",Toast.LENGTH_SHORT).show()
                        constraintset.clone(constraintLayout)
                        constraintset.connect(thanksTxt.id,
                            ConstraintSet.TOP,
                            imageView.id,
                            ConstraintSet.BOTTOM
                        )

                        constraintset.applyTo(constraintLayout)
//
                    }

                } else{
                    feedbackbtn.isEnabled = false
                    feedbackbtn.setTextColor(resources.getColor(R.color.color_black_tertiary))
                    feedbackbtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.color_primary_dark_gray)
                }
            }

        )

//        if (feedbackbtn.isEnabled){
//            Toast.makeText(requireContext(),"hallo enabled bois",Toast.LENGTH_SHORT).show()

       // }
//        if (metfeedbacknegative.text.isNotEmpty()){
//            Toast.makeText(requireContext(),"hallo text not empty",Toast.LENGTH_SHORT).show()
//          feedbackbtn.isEnabled = true
//          feedbackbtn.setTextColor(resources.getColor(R.color.white))
//            feedbackbtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.color_secondary_blue)
//        }
//        else {
//            Toast.makeText(requireContext(),"hallo text empty",Toast.LENGTH_SHORT).show()
//            feedbackbtn.isEnabled = false
//            feedbackbtn.setTextColor(resources.getColor(R.color.color_black_tertiary))
//            feedbackbtn.backgroundTintList = ContextCompat.getColorStateList(requireContext(),color.color_primary_dark_gray)
//        }
    }
}