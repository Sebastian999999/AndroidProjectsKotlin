package com.hammadirfan.smdproject

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class OrderAdapter(private val orders: MutableList<Orders> ,private val onOrderClicked: (Order_s) -> Unit) : RecyclerView.Adapter<OrderAdapter.OrderViewHolder>() {

    class OrderViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val orderId: TextView = view.findViewById(R.id.order_id)
        val customerName: TextView = view.findViewById(R.id.customer_name)
        val orderDate: TextView = view.findViewById(R.id.order_date)
        val orderTime: TextView = view.findViewById(R.id.order_time)
        val amount: TextView = view.findViewById(R.id.amount)
        val type: TextView = view.findViewById(R.id.type)
        val btnAccept: ImageButton = view.findViewById(R.id.btnAccept)
        val btnReject: ImageButton = view.findViewById(R.id.btnReject)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): OrderViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.order_item, parent, false)
        return OrderViewHolder(view)
    }

    override fun onBindViewHolder(holder: OrderViewHolder, position: Int) {
        val order = orders[position]
        holder.orderId.text = "Order ID: ${order.orderId}"
        holder.customerName.text = "Customer Name: ${order.customerName}"
        holder.orderDate.text = "Order Date: ${order.orderDate}"
        holder.orderTime.text = "Order Time: ${order.orderTime}"
        holder.amount.text = "Amount: ${order.amount}"
        holder.type.text = "Type: ${order.type}"

        val orderItems = mutableListOf(
            Item_s(1,"Lays",1),
            Item_s(2,"Pepsi",2)
        )// Assume this fetches items from a source or static data
        val detailedOrder = Order_s(order.orderId, orderItems)


        // Set click listeners
        holder.itemView.setOnClickListener {
            onOrderClicked(detailedOrder)
        }

        holder.btnAccept.setOnClickListener {
            removeOrder(position)
        }

        holder.btnReject.setOnClickListener {
            removeOrder(position)
        }
    }

    override fun getItemCount() = orders.size

    private fun removeOrder(position: Int) {
        orders.removeAt(position)
        notifyItemRemoved(position)
        notifyItemRangeChanged(position, orders.size)
    }
}
