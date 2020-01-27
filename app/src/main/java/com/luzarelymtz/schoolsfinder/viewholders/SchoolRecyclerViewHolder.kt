package com.luzarelymtz.schoolsfinder.viewholders

import android.view.View
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.luzarelymtz.schoolsfinder.R

class SchoolRecyclerViewHolder(view: View) : RecyclerView.ViewHolder(view) {


    interface ItemClickListener {
        fun onItemClick(view: View, position: Int)
    }

    val title: TextView = view.findViewById(R.id.SchoolTitle)
    val address: TextView = view.findViewById(R.id.SchoolAddress)

    init {

    }

}