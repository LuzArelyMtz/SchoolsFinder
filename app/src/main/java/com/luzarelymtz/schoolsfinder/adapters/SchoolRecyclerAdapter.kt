package com.luzarelymtz.schoolsfinder.adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.luzarelymtz.schoolsfinder.R
import com.luzarelymtz.schoolsfinder.model.School
import com.luzarelymtz.schoolsfinder.viewholders.SchoolRecyclerViewHolder
import java.util.*

class SchoolRecyclerAdapter(private val context: Context, private val itemClickListener: SchoolRecyclerViewHolder.ItemClickListener, private var schoolItem:List<School>) : RecyclerView.Adapter<SchoolRecyclerViewHolder>() {

    private var mRecyclerView : RecyclerView? = null

    override fun onAttachedToRecyclerView(recyclerView: RecyclerView) {
        super.onAttachedToRecyclerView(recyclerView)
        mRecyclerView = recyclerView
    }

    override fun onDetachedFromRecyclerView(recyclerView: RecyclerView) {
        super.onDetachedFromRecyclerView(recyclerView)
        mRecyclerView = null

    }
     fun setNewList(list: List<School>) {
        schoolItem=list
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holderSchool: SchoolRecyclerViewHolder, position: Int) {
        holderSchool?.let {
            it.title.text = schoolItem.get(position).getName()
            it.address.text=schoolItem.get(position).getAddress()
        }
    }

    override fun getItemCount(): Int {
        return schoolItem.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): SchoolRecyclerViewHolder {

        val layoutInflater = LayoutInflater.from(context)
        val mView = layoutInflater.inflate(R.layout.cardview_school, parent, false)

        mView.setOnClickListener { view ->
            mRecyclerView?.let {
                itemClickListener.onItemClick(view, it.getChildAdapterPosition(view))
            }
        }

        return SchoolRecyclerViewHolder(mView)
    }

}