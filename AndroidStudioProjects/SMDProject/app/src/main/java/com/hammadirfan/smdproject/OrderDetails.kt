package com.hammadirfan.smdproject

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

// TODO: Rename parameter arguments, choose names that match
// the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

/**
 * A simple [Fragment] subclass.
 * Use the [OrderDetails.newInstance] factory method to
 * create an instance of this fragment.
 */
class OrderDetails : Fragment() {
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
    private lateinit var order: Order_s

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        val view = inflater.inflate(R.layout.fragment_order_details, container, false)
        order = arguments?.getSerializable("order") as Order_s

        val orderIdTextView: TextView = view.findViewById(R.id.tvOrderID)
        orderIdTextView.text = "Order ID: ${order.orderId}"

        recyclerView = view.findViewById(R.id.itemsRecyclerView)
        recyclerView.layoutManager = LinearLayoutManager(context)
        recyclerView.adapter = ItemAdapter(order.items)

        return view
    }

    companion object {
        fun newInstance(order: Order_s): OrderDetails {
            val fragment = OrderDetails()
            val args = Bundle()
            args.putSerializable("order", order)
            fragment.arguments = args
            return fragment
        }
    }
}