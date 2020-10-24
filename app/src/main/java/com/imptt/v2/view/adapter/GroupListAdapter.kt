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
import com.kylindev.pttlib.service.model.Channel
import kotlin.collections.ArrayList

/**
 *  author : apm29[ciih]
 *  date : 2020/10/10 10:17 AM
 *  description :
 */
class GroupListAdapter(
    private var channelList: ArrayList<Channel>,
    private val layoutInflater: LayoutInflater,
    private val onItemRoute: ((Channel, View) -> Unit)? = null,
    private val onChannelListenChange: ((Channel, Boolean, View) -> Unit)? = null,
    private val onChannelSpeakChange: ((Channel, Boolean, View) -> Unit)? = null,
    private var listenChannels: MutableList<Int>?,
    private var currentChannel: Channel? = null
) : RecyclerView.Adapter<GroupListAdapter.VH>() {

    private var list: ArrayList<Channel>
        get() {
            channelList.sortWith { one, other ->
                one.id - other.id
            }
            return channelList
        }
        set(value) {
            channelList = value
        }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view = layoutInflater.inflate(R.layout.group_item_layout, parent, false)
        return VH(view)
    }


    @SuppressLint("SetTextI18n")
    override fun onBindViewHolder(holder: VH, position: Int) {
        val channel = list[position]
        val isCurrentChannel = currentChannel != null && currentChannel?.id == channel.id
        holder.textViewGroupName.text =
            if (isCurrentChannel) getCurrentChannelText(channel,holder.itemView) else channel.name
        holder.textViewGroupUserCount.text = "${channel.memberCount}人"
        holder.itemView.setOnClickListener {
            onItemRoute?.invoke(channel, it)
        }

        holder.checkboxCurrentChannel.visibility =
            if (isCurrentChannel) View.VISIBLE else View.INVISIBLE

        holder.checkboxMuteChannel.isEnabled =
            !isCurrentChannel

        holder.checkboxMuteChannel.isChecked =
            listenChannels?.contains(channel.id) == true || isCurrentChannel

        holder.checkboxCurrentChannel.isEnabled = false

        holder.checkboxCurrentChannel.setOnCheckedChangeListener { buttonView, isChecked ->
            onChannelSpeakChange?.invoke(channel, isChecked, buttonView)
        }

        holder.checkboxMuteChannel.setOnCheckedChangeListener { buttonView, isChecked ->
            onChannelListenChange?.invoke(channel, isChecked, buttonView)
        }
    }

    private fun getCurrentChannelText(channel: Channel, itemView: View): CharSequence {
        return SpannableStringBuilder().append(
            channel.name
        ).append(
            "(当前频道)",
            TextAppearanceSpan(
                itemView.context,
                R.style.TextAppearance_AppCompat_Button
            ),
            SpannableStringBuilder.SPAN_EXCLUSIVE_EXCLUSIVE
        )
    }


    override fun getItemCount(): Int {
        return list.size
    }

    fun newList(groups: ArrayList<Channel>, listenChannels: MutableList<Int>?) {
        this.list = groups
        this.listenChannels = listenChannels
        notifyDataSetChanged()
    }

    fun changeChannel(channel: Channel) {
        this.currentChannel = channel
        notifyDataSetChanged()
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewGroupName: TextView = itemView.findViewById(R.id.textViewGroupName)
        val textViewGroupUserCount: TextView = itemView.findViewById(R.id.textViewGroupUserCount)
        val imageViewGroupType: ImageView = itemView.findViewById(R.id.imageViewGroupType)
        val checkboxCurrentChannel: CheckBox = itemView.findViewById(R.id.checkboxCurrentChannel)
        val checkboxMuteChannel: CheckBox = itemView.findViewById(R.id.checkboxMuteChannel)
    }
}

