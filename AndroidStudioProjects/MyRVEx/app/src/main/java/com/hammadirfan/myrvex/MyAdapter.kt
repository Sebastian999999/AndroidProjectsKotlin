package com.hammadirfan.myrvex

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class MyAdapter(list: ArrayList<Model>,c: Context) ://MyAdapter child class of RecyclerView.Adapter<MyAdapter.MyViewHolder>()
    RecyclerView.Adapter<MyAdapter.MyViewHolder>() {
        val list:ArrayList<Model> = list
                var context:Context = c
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val v:View = LayoutInflater
                                .from(context)
                                .inflate(R.layout.row,parent,false)
        return MyViewHolder(v)

    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {

        holder.name.text=list.get(position).name
        holder.phno.text=list.get(position).phno
        holder.email.text=list.get(position).email
        holder.row.setOnClickListener(){
            val i = android.content.Intent(context,ViewContent::class.java)
            i.putExtra("name",list.get(position).name)
            i.putExtra("phone",list.get(position).phno)
            i.putExtra("email",list.get(position).email)
            context.startActivity(i)

        }

        holder.row.setOnLongClickListener(){
            list.removeAt(position)
            notifyDataSetChanged()
            return@setOnLongClickListener true
        }
    }

    class MyViewHolder : RecyclerView.ViewHolder {
        constructor(itemView:View) : super(itemView)
        var row:LinearLayout=itemView.findViewById<LinearLayout>(R.id.row)
        var img:ImageView = itemView.findViewById<ImageView>(R.id.imageView)
        var name:TextView = itemView.findViewById<TextView>(R.id.name)
        var phno:TextView= itemView.findViewById<TextView>(R.id.phno)
        var email:TextView=itemView.findViewById<TextView>(R.id.email)
    }

}


