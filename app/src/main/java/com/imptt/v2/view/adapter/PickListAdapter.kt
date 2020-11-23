package com.imptt.v2.view.adapter

import android.annotation.SuppressLint
import android.text.SpannableStringBuilder
import android.text.style.TextAppearanceSpan
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.imptt.v2.R
import com.imptt.v2.data.model.v2.ChannelUser
import com.kylindev.pttlib.service.model.Channel
import com.kylindev.pttlib.service.model.User
import kotlin.collections.ArrayList

/**
 *  author : apm29[ciih]
 *  date : 2020/10/10 10:17 AM
 *  description :
 */
class PickListAdapter(
    private var list: List<ChannelUser>,
    private val layoutInflater: LayoutInflater,
    private val onItemRoute: ((ChannelUser, View) -> Unit)? = null,
) : RecyclerView.Adapter<PickListAdapter.VH>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = layoutInflater.inflate(R.layout.pick_user_layout, parent, false)
        return VH(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val user = list[position]
        holder.textViewUserName.text = user.nickName
        holder.textViewUserDesc.text = user.userId
        holder.itemView.setOnClickListener {
            onItemRoute?.invoke(user,holder.itemView)
        }
    }


    override fun getItemCount(): Int {
        return list.size
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewAvatar: ImageView? = itemView.findViewById(R.id.imageViewAvatar)
        val textViewUserName: TextView = itemView.findViewById(R.id.textViewUserName)
        val textViewUserDesc: TextView = itemView.findViewById(R.id.textViewUserDesc)
    }
}

