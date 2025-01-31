package com.hammadirfan.smdproject

import android.app.TimePickerDialog
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.TextView
import android.widget.Toast
import androidx.cardview.widget.CardView
import java.util.Calendar

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderPickUpFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderPickUpFragment : Fragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_order_pick_up, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        var TimeBtn = view.findViewById<TextView>(R.id.TimeBtn)
        var calendar:Calendar = Calendar.getInstance()
        var CurrentHr = calendar.get(Calendar.HOUR_OF_DAY)
        var CurrentMin = calendar.get(Calendar.MINUTE)
        //Toast.makeText(context, CurrentHr.toString() + ":" + CurrentMin.toString(), Toast.LENGTH_SHORT).show()
        // Inflate the layout for this fragment
        TimeBtn.setOnClickListener {
            //Toast.makeText(context, CurrentHr.toString() + ":" + CurrentMin.toString(), Toast.LENGTH_SHORT).show()
            var dialog:TimePickerDialog = TimePickerDialog(
                view.context,
                TimePickerDialog.OnTimeSetListener { view, hourOfDay, minute ->
                    val formattedHour = if (hourOfDay > 12) hourOfDay - 12 else hourOfDay
                    val amPm = if (hourOfDay >= 12) "PM" else "AM"
                    TimeBtn.text = String.format("%02d:%02d %s", formattedHour, minute, amPm)
                },
                CurrentHr,
                CurrentMin,
                false
            )
            dialog.show()
        }
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrderPickUpFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrderPickUpFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}