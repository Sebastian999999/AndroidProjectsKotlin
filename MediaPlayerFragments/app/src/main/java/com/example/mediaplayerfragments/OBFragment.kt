package com.example.mediaplayerfragments

import android.content.Context
import android.graphics.drawable.GradientDrawable
import android.graphics.drawable.StateListDrawable
import android.os.Bundle
import android.text.Spannable
import android.text.SpannableString
import android.text.style.UnderlineSpan
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.Toast
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.constraintlayout.widget.ConstraintSet
import androidx.core.content.ContextCompat
import androidx.core.view.marginLeft
import androidx.core.view.marginStart
import androidx.navigation.fragment.findNavController
import androidx.transition.Visibility
import androidx.viewpager2.widget.ViewPager2
import com.example.mediaplayerfragments.databinding.FragmentOBBinding
import com.google.android.material.button.MaterialButton
import com.google.android.material.tabs.TabLayoutMediator

class OBFragment : Fragment() {
    private lateinit var viewFrag: FragmentOBBinding
    private val imageList = listOf(
        R.mipmap.ob1_pic,
        R.mipmap.ob_pic2,
        R.mipmap.ob_pic3,
        R.mipmap.ob_pic4,
        R.mipmap.ob4_pic
    )

    private val textList = listOf(
        R.string.ultra_hd_video_player, R.string.supports_all_formats,
        R.string.background_audio_player, R.string.lorem_ipsum_dolar_sit_amit,
        R.string.audio_player, R.string.supports_all_formats,
        R.string.whatsApp_status_saver, R.string.create_documents_from_scanned_pages,
        R.string.multiple_themes_wallpaper, R.string.create_documents_from_scanned_pages
    )

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        viewFrag = FragmentOBBinding.inflate(inflater, container, false)
        val spannable = SpannableString(viewFrag.mtvskip.text)
        spannable.setSpan(UnderlineSpan(),0, spannable.length, Spannable.SPAN_INCLUSIVE_INCLUSIVE)
        viewFrag.mtvskip.text = spannable
        // Pass context safely
        //val safeContext = requireContext()
        viewFrag.flbannerad.visibility = View.GONE
        val adapter = ImageSwiperAdapter(imageList)
        viewFrag.vp2imageswiper.adapter = adapter
        TabLayoutMediator(viewFrag.tabLayout, viewFrag.vp2imageswiper) { tab, position ->
            tab.customView = createDotView(requireContext())
        }.attach()
        viewFrag.vp2imageswiper.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                updateConstraints(position)
            }
        })

        viewFrag.mtvskip2.visibility = View.INVISIBLE
        viewFrag.mbnext2.visibility = View.INVISIBLE
        viewFrag.mtvnext.setOnClickListener {
            if (viewFrag.mtvskip.visibility==View.INVISIBLE){
                findNavController().navigate(R.id.action_OBFragment_to_InAppPremiumBannerFragment2)
            }
            if (viewFrag.vp2imageswiper.currentItem < imageList.size - 1) {
                viewFrag.vp2imageswiper.currentItem += 1
            } else {
                viewFrag.mtvskip.visibility = View.INVISIBLE
            }
        }

        viewFrag.mtvskip.setOnClickListener {
            findNavController().navigate(R.id.action_OBFragment_to_InAppPremiumBannerFragment2)
        }
        return viewFrag.root
    }

    private fun updateConstraints(currentItem: Int) {
        if (currentItem == imageList.size - 1) {
            viewFrag.mtvskip.visibility = View.INVISIBLE
        } else {
            viewFrag.mtvskip.visibility = View.VISIBLE
        }

        // Ensure position does not go out of bounds
        val textIndex = currentItem * 2
        if (textIndex < textList.size - 1) {
            viewFrag.mtv1.text = getString(textList[textIndex])
            viewFrag.mtv2.text = getString(textList[textIndex + 1])
        }
        val constraintLayout = viewFrag.clbackground
        val constraintSet = ConstraintSet()
        constraintSet.clone(constraintLayout)
        //constraintSet.setDimensionRatio(viewFrag.vp2imageswiper.get(R.id.msivfragmentpics))
        if (viewFrag.flbannerad.visibility == View.GONE) {
            Toast.makeText(requireContext(), "Banner Ad Hidden", Toast.LENGTH_SHORT).show()


            // Update ViewPager2 constraint to parent bottom

            constraintSet.connect(
                viewFrag.vp2imageswiper.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
            constraintSet.connect(
                viewFrag.tabLayout.id,
                ConstraintSet.BOTTOM,
                ConstraintSet.PARENT_ID,
                ConstraintSet.BOTTOM
            )
        } else {
            Toast.makeText(requireContext(), "Banner Ad Shown", Toast.LENGTH_SHORT).show()
            //viewFrag.flbannerad.visibility = View.VISIBLE
            // Restore original constraints (connect to flbannerad)
            constraintSet.connect(
                viewFrag.vp2imageswiper.id,
                ConstraintSet.BOTTOM,
                viewFrag.flbannerad.id,
                ConstraintSet.TOP
            )
            constraintSet.connect(
                viewFrag.tabLayout.id,
                ConstraintSet.BOTTOM,
                viewFrag.flbannerad.id,
                ConstraintSet.TOP
            )
        }

        constraintSet.applyTo(constraintLayout)
    }

    private fun createDotView(context: Context): View {
        val dot = View(context)
        // Define dot size (10dp in pixels)
        val size = (10 * context.resources.displayMetrics.density).toInt()
        // Use LinearLayout.LayoutParams (or another MarginLayoutParams) to support margins
        val layoutParams = ConstraintLayout.LayoutParams(size, size)
        // Set a smaller margin to reduce space between dots (e.g., 1dp)
        val margin = (1 * context.resources.displayMetrics.density).toInt()
        layoutParams.marginStart = margin
        dot.layoutParams = layoutParams

        // Colors for selected and unselected states
        val selectedColor = ContextCompat.getColor(context, R.color.color_primary_blue)
        val unselectedColor = ContextCompat.getColor(context, R.color.white)

        // Create drawables for each state
        val selectedDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(selectedColor)
            setSize(size, size)
        }
        val unselectedDrawable = GradientDrawable().apply {
            shape = GradientDrawable.OVAL
            setColor(unselectedColor)
            setStroke(5, ContextCompat.getColor(context, R.color.color_primary_gray))
            setSize(size, size)
        }

        // Create a StateListDrawable to switch between states
        val stateListDrawable = StateListDrawable().apply {
            addState(intArrayOf(android.R.attr.state_selected), selectedDrawable)
            addState(intArrayOf(), unselectedDrawable)
        }
        dot.background = stateListDrawable
        return dot
    }

}
