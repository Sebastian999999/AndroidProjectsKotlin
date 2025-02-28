package com.example.rcvprac1

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView

class NewsAdapter(private val news:ArrayList<News>): RecyclerView.Adapter<NewsAdapter.MyViewHolder>(){

    private var mlistener: OnItemClickListener? = null
    interface OnItemClickListener{
        fun onItemClick(position: Int)
    }

    fun setOnItemClickListener(listener: OnItemClickListener){
        mlistener = listener
    }

    fun deleteItem(i: Int){
        news.removeAt(i)
        notifyDataSetChanged()
    }

    fun getAllNews() : ArrayList<News>{
        return news
    }

    fun archiveItem(position:Int){
        val newsItem = news[position]
        news.removeAt(position)
        news.add(newsItem)
        notifyDataSetChanged()
    }
    override fun onCreateViewHolder(parent:ViewGroup, viewType: Int): MyViewHolder{
        var view = LayoutInflater.from(parent.context).inflate(R.layout.rcv_item,parent,false)
        return MyViewHolder(view,mlistener)

    }

    override fun onBindViewHolder(holder: MyViewHolder, position: Int){
        holder.image.setImageResource(news[position].imageId)
        holder.newsText.text = news[position].newsText
    }
    override fun getItemCount(): Int{
        return news.size
    }


    inner class MyViewHolder(itemView: View, listener: OnItemClickListener?): RecyclerView.ViewHolder(itemView){
        var image = itemView.findViewById<ImageView>(R.id.ivnews)
        var newsText = itemView.findViewById<TextView>(R.id.mtvnews)
        init{
            itemView.setOnClickListener{
                listener?.onItemClick(adapterPosition)
            }
        }
    }

}