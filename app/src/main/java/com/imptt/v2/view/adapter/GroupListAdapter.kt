package com.imptt.v2.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.imptt.v2.R
import com.imptt.v2.core.websocket.Group

/**
 *  author : ciih
 *  date : 2020/10/10 10:17 AM
 *  description :
 */
class GroupListAdapter constructor(
    private var list: ArrayList<Group>,
    private val layoutInflater: LayoutInflater
) : RecyclerView.Adapter<VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = layoutInflater.inflate(R.layout.group_item_layout,parent,false)
        return VH(view)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.textViewGroupName.text = list[position].groupId
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun newList(groups: java.util.ArrayList<Group>) {
        this.list = groups
        notifyDataSetChanged()
    }

}

class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
    val textViewGroupName:TextView = itemView.findViewById(R.id.textViewGroupName)
}