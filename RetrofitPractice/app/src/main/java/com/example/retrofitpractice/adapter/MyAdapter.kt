package com.example.retrofitpractice.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.retrofitpractice.Models.Post
import com.example.retrofitpractice.R

class MyAdapter: RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
    private var myList = emptyList<Post>()
    inner class MyViewHolder(itemView :View):RecyclerView.ViewHolder(itemView)

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        holder.itemView.findViewById<TextView>(R.id.userId)
        holder.itemView.findViewById<TextView>(R.id.postId)
        holder.itemView.findViewById<TextView>(R.id.titletext)
        holder.itemView.findViewById<TextView>(R.id.bodytext)
    }
    fun setData(mylist : List<Post>){
        myList = mylist
        notifyDataSetChanged()
    }
    override fun getItemCount(): Int {
        return myList.size
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        return MyViewHolder(LayoutInflater.from(parent.context).inflate(R.layout.item_row,parent,false))
    }
}