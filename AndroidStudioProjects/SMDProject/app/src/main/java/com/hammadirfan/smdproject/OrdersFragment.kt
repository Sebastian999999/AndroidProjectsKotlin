package com.hammadirfan.smdproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrdersFragment.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrdersFragment : Fragment() {
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

    private lateinit var recyclerView: RecyclerView
    private lateinit var adapter: OrderAdapter

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_orders, container, false)
        recyclerView = view.findViewById(R.id.orders_recyclerview)
        recyclerView.layoutManager = LinearLayoutManager(context)

        // Sample data based on your screenshot
        val sampleOrders = mutableListOf(
            Orders("HD_30131713382529202", "aftab23", "2024-04-18", "00:35:29", 40.23, "Home Delivery"),
            Orders("HD_18471713390948525", "aftab23", "2024-04-18", "02:55:48", 190.46, "Home Delivery"),
            Orders("HD_30131713382529202", "aftab23", "2024-04-18", "00:35:29", 40.23, "Home Delivery"),
            Orders("HD_18471713390948525", "aftab23", "2024-04-18", "02:55:48", 190.46, "Home Delivery"),
            Orders("HD_30131713382529202", "aftab23", "2024-04-18", "00:35:29", 40.23, "Home Delivery"),
            Orders("HD_18471713390948525", "aftab23", "2024-04-18", "02:55:48", 190.46, "Home Delivery"),
            Orders("HD_30131713382529202", "aftab23", "2024-04-18", "00:35:29", 40.23, "Home Delivery"),
            Orders("HD_18471713390948525", "aftab23", "2024-04-18", "02:55:48", 190.46, "Home Delivery")
        )

        // Conversion from Orders to Order_s and loading of corresponding items
        recyclerView.adapter = OrderAdapter(sampleOrders) { order_s ->
            (activity as? ManagerDashboard)?.showOrderDetails(order_s)
        }

        return view
    }

    private fun showOrderDetails(order: Order_s) {
        // Assume ManagerDashboard or whichever activity is hosting this
        (activity as? ManagerDashboard)?.showOrderDetails(order)
    }

    companion object {
        /**
         * Use this factory method to create a new instance of
         * this fragment using the provided parameters.
         *
         * @param param1 Parameter 1.
         * @param param2 Parameter 2.
         * @return A new instance of fragment OrdersFragment.
         */
        // TODO: Rename and change types and number of parameters
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            OrdersFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }
}