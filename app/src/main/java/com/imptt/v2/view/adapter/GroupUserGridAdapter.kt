package com.imptt.v2.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.imptt.v2.R
import com.imptt.v2.data.model.v2.ChannelUser
import com.kylindev.pttlib.service.model.User

class GroupUserGridAdapter(
    private var users: List<ChannelUser>,
    private val layoutInflater: LayoutInflater,
    private val onUserClick: ((ChannelUser, View)->Unit)?=null,
    private val onAddClick: ((View)->Unit)?=null
) : RecyclerView.Adapter<GroupUserGridAdapter.VH>() {

    companion object{
        const val TYPE_ADD = 2001
    }

    override fun getItemViewType(position: Int): Int {
        if(position == users.size){
            return TYPE_ADD
        }
        return super.getItemViewType(position)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = when(viewType){
            TYPE_ADD->{
                layoutInflater.inflate(R.layout.group_user_add_layout,parent,false)
            }
            else->{
                layoutInflater.inflate(R.layout.group_user_layout,parent,false)
            }
        }
        //设置itemView宽度为父容器的1/4
        view.layoutParams.width = View.MeasureSpec.makeMeasureSpec(parent.width/4,View.MeasureSpec.EXACTLY)
        return VH(view)
    }


    override fun onBindViewHolder(holder: VH, position: Int) {
        if(getItemViewType(position) != TYPE_ADD) {
            val user = users[position]
            holder.itemView.setOnClickListener {
                onUserClick?.invoke(user, it)
            }
            holder.textViewUserName.text = user.nickName
            holder.textViewUserDesc.text = user.userId
        }else{
            holder.itemView.setOnClickListener {
                onAddClick?.invoke(it)
            }
        }
    }


    override fun getItemCount(): Int {
        return users.size + 1
    }

    fun newList(users: List<ChannelUser>) {
        this.users = users
        notifyDataSetChanged()
    }
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val imageViewAvatar: ImageView? = itemView.findViewById(R.id.imageViewAvatar)
        val textViewUserName: TextView = itemView.findViewById(R.id.textViewUserName)
        val textViewUserDesc: TextView = itemView.findViewById(R.id.textViewUserDesc)
    }
}