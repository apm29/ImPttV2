package com.imptt.v2.view.adapter

import android.annotation.SuppressLint
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.imptt.v2.R
import com.imptt.v2.data.entity.Group
import com.imptt.v2.data.entity.GroupWithUsers
import com.imptt.v2.di.GlideApp
import java.util.*
import kotlin.collections.ArrayList

/**
 *  author : apm29[ciih]
 *  date : 2020/10/10 10:17 AM
 *  description :
 */
class GroupListAdapter constructor(
    private var list: ArrayList<GroupWithUsers>,
    private val layoutInflater: LayoutInflater,
    private val onItemRoute: ((GroupWithUsers, View) -> Unit)? = null
) : RecyclerView.Adapter<GroupListAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = layoutInflater.inflate(R.layout.group_item_layout, parent, false)
        return VH(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        holder.textViewGroupName.text = list[position].group.groupName
        holder.textViewGroupUserCount.text = "${list[position].users.size}人"
        if (!list[position].group.groupIcon.isNullOrBlank()) {
            GlideApp.with(holder.imageViewGroupType)
                .load(list[position].group.groupIcon)
                .into(holder.imageViewGroupType)
        }
        holder.itemView.setOnClickListener {
            onItemRoute?.invoke(list[position], it)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun newList(groups: ArrayList<GroupWithUsers>) {
        this.list = groups
        notifyDataSetChanged()
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewGroupName: TextView = itemView.findViewById(R.id.textViewGroupName)
        val textViewGroupUserCount: TextView = itemView.findViewById(R.id.textViewGroupUserCount)
        val imageViewGroupType: ImageView = itemView.findViewById(R.id.imageViewGroupType)
    }
}

