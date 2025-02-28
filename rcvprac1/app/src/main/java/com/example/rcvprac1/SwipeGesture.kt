package com.example.rcvprac1

import android.graphics.Canvas
import android.graphics.Color
import android.graphics.Paint
import androidx.annotation.InspectableProperty
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.RecyclerView

abstract class SwipeGesture : ItemTouchHelper.SimpleCallback(ItemTouchHelper.UP
        or ItemTouchHelper.DOWN or ItemTouchHelper.END or ItemTouchHelper.START,
    ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT) {

    override fun onSwiped(viewHolder: RecyclerView.ViewHolder , direction: Int){

    }
    override fun onMove(
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        target: RecyclerView.ViewHolder): Boolean{
        return true
    }

    override fun onChildDraw(
        c: Canvas,
        recyclerView: RecyclerView,
        viewHolder: RecyclerView.ViewHolder,
        dx: Float,
        dy: Float,
        actionState: Int,
        isCurrentlyActive: Boolean
    ){
        var paint = Paint()
        val view = viewHolder.itemView
        if (dx>0){
            paint.color = Color.GREEN
        }
        else {
            paint.color = Color.RED
        }
        c.drawRect(view.left.toFloat(),view.top.toFloat(),
            view.right.toFloat(),view.bottom.toFloat(),
            paint)
        super.onChildDraw(c,recyclerView,viewHolder,dx,dy,actionState,isCurrentlyActive)
    }
}