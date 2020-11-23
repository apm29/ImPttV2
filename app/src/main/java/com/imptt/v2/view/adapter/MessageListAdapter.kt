package com.imptt.v2.view.adapter

import android.graphics.drawable.AnimationDrawable
import android.text.format.Formatter
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.imptt.v2.R
import com.imptt.v2.data.entity.FileMessage
import com.imptt.v2.data.model.message.MessageType
import com.imptt.v2.di.GlideApp
import com.imptt.v2.utils.clamp
import com.imptt.v2.utils.gone
import com.imptt.v2.utils.loadImageData
import com.imptt.v2.utils.visible
import com.imptt.v2.view.adapter.helper.MessageDiffer
import com.imptt.v2.widget.FileDownloadImageView
import com.kylindev.pttlib.db.ChatMessageBean
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.abs

/**
 *  author : apm29[ciih]
 *  date : 2020/10/10 10:17 AM
 *  description :
 */
class MessageListAdapter constructor(
    val messages: MutableList<Any>,
    private val layoutInflater: LayoutInflater,
    private val myId: Int,
    private var currentPlayPosition: Int = -1,
    private val onMessageClicked: ((Any, View, Int) -> Unit)? = null,
    private val onUserClicked: ((Any, View) -> Unit)? = null,
) : RecyclerView.Adapter<MessageListAdapter.VH>() {

    private val timeAnchors = Stack<Int>()

    init {
        setHasStableIds(true)
        createTimeStampAnchor()
    }

    override fun getItemViewType(position: Int): Int {
        val message: Any = getMessageByPosition(position)
        if (message is ChatMessageBean) {
            val fromSelf = message.uid != myId
            if (message.voice != null
                && message.voice.isNotEmpty()
                && message.text == null
            ) {
                return if (fromSelf)
                    MessageType.AUDIO_ME.type
                else
                    MessageType.AUDIO_OTHER.type
            } else if (
                message.videopath != null
                && message.videopath.isNotEmpty()
            ) {
                return if (fromSelf)
                    MessageType.VIDEO_ME.type
                else
                    MessageType.VIDEO_OTHER.type
            } else if (
                message.imagepath != null
                && message.imagepath.isNotEmpty()
            ) {
                return if (fromSelf)
                    MessageType.IMAGE_ME.type
                else
                    MessageType.IMAGE_OTHER.type
            } else if (
                message.text != null
                && message.text.isNotEmpty()
            ) {
                return if (fromSelf)
                    MessageType.TEXT_ME.type
                else
                    MessageType.TEXT_OTHER.type
            }
        }
        //1:图片  2:word文档  3:表格  4:PDF  5:文本 6:txt
        if (message is FileMessage) {
            val fromSelf = myId.toString() != message.tUserId
            when (message.type) {
                //1 -> {
                //    return if (fromSelf)
                //        MessageType.IMAGE_ME.type
                //    else
                //        MessageType.IMAGE_OTHER.type
                //}
                5 -> {
                    return if (fromSelf)
                        MessageType.TEXT_ME.type
                    else
                        MessageType.TEXT_OTHER.type
                }
                else -> {
                    return if (fromSelf)
                        MessageType.FILE_ME.type
                    else
                        MessageType.FILE_OTHER.type
                }
            }
        }
        //倒序取消息
        //映射关系:
        //          MSG1 MSG2 MSG3 MSG4
        //            3    2    1    0
        return MessageType.UNKNOWN.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view: View = when (viewType) {
            MessageType.AUDIO_ME.type -> {
                layoutInflater.inflate(R.layout.message_audio_layout, parent, false)
            }
            MessageType.AUDIO_OTHER.type -> {
                layoutInflater.inflate(R.layout.message_audio_reverse_layout, parent, false)
            }
            MessageType.TEXT_ME.type -> {
                layoutInflater.inflate(R.layout.message_text_layout, parent, false)
            }
            MessageType.TEXT_OTHER.type -> {
                layoutInflater.inflate(R.layout.message_text_reverse_layout, parent, false)
            }
            MessageType.FILE_ME.type -> {
                layoutInflater.inflate(R.layout.message_file_layout, parent, false)
            }
            MessageType.FILE_OTHER.type -> {
                layoutInflater.inflate(R.layout.message_file_reverse_layout, parent, false)
            }
            else -> {
                //throw IllegalArgumentException("未知的消息类型:$viewType")
                View(parent.context)
            }
        }

        return VH(view)
    }

    private val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())

    override fun onBindViewHolder(holder: VH, position: Int) {
        val contentType = getItemViewType(position)
        val message: Any = getMessageByPosition(position)
        if (message is ChatMessageBean) {
            holder.textViewUserName?.text = message.nick
            holder.textViewTime?.text = simpleDateFormat.format(Date(message.time))
            holder.imageViewAvatar?.loadImageData(message.avatar)
            holder.imageViewAvatar?.setOnClickListener {
                onUserClicked?.invoke(messages[position], it)
            }
            holder.imageViewPlay?.id = position

            if (contentType == MessageType.AUDIO_OTHER.type || contentType == MessageType.AUDIO_ME.type) {
                //语音
                holder.textViewDuration?.text =
                    "${getVoiceSize(holder, message)} | ${getVoiceDuration(message.voice)}秒"
                holder.layoutMessageBody?.setOnClickListener {
                    onMessageClicked?.invoke(message, it, position)
                }
                if (currentPlayPosition == position) {
                    //播放中
                    holder.imageViewPlay?.setImageResource(R.drawable.anim_voice_play_receiver)
                    val drawable = holder.imageViewPlay?.drawable as? AnimationDrawable
                    drawable?.start()
                } else {
                    //停止的
                    holder.imageViewPlay?.setImageResource(R.mipmap.volumehigh)
                }
            } else if (contentType == MessageType.TEXT_OTHER.type || contentType == MessageType.TEXT_ME.type) {
                //文本
                holder.textViewContent?.text = message.text
            }

            //时间
            val indexOf = timeAnchors.toMutableList().indexOf(position)
            if (indexOf < 0) {
                holder.textViewTime?.gone()
            } else {
                holder.textViewTime?.visible()
            }
        }
        if (message is FileMessage) {
            holder.textViewUserName?.text = message.nickName
            holder.textViewTime?.text = simpleDateFormat.format(message.date)
            holder.imageViewAvatar?.let {
                message.avatar?.let {
                    GlideApp.with(holder.imageViewAvatar)
                        .load(message.avatar)
                        .into( holder.imageViewAvatar)
                }
            }
            holder.imageViewAvatar?.setOnClickListener {
                onUserClicked?.invoke(messages[position], it)
            }
            holder.imageViewFile?.missionId = message.id

            if (contentType == MessageType.TEXT_OTHER.type || contentType == MessageType.TEXT_ME.type) {
                //文本
                holder.textViewContent?.text = message.content
            }

            if (contentType == MessageType.FILE_OTHER.type || contentType == MessageType.FILE_ME.type) {
                //文本
                holder.textViewContent?.text =
                    message.fileName ?: message.content.substring(message.content.lastIndexOf("/"))
            }

            //时间
            val indexOf = timeAnchors.toMutableList().indexOf(position)
            if (indexOf < 0) {
                holder.textViewTime?.gone()
            } else {
                holder.textViewTime?.visible()
            }
        }
    }

    private fun createTimeStampAnchor() {
        val times: List<Long> = messages.map {
            when (it) {
                is ChatMessageBean -> it.time
                is FileMessage -> it.date.time
                else -> 0
            } ?: 0L
        }
        timeAnchors.clear()
        times.forEachIndexed { index: Int, time: Long ->
            when {
                timeAnchors.isEmpty() -> {
                    timeAnchors.push(index)
                }
                abs(times[timeAnchors.peek()] - time) > 60 * 1000 -> {
                    timeAnchors.push(index)
                }
                abs(timeAnchors.peek() - index) >= 4 -> {
                    timeAnchors.push(index)
                }
                index == times.size - 1 -> {
                    timeAnchors.push(index)
                }
            }
        }
    }

    private fun getMessageByPosition(position: Int) = messages[position]

    private fun getVoiceSize(
        holder: VH,
        message: ChatMessageBean
    ): String? {
        return Formatter.formatFileSize(
            holder.itemView.context,
            message.voice.size.toLong()
        )
    }


    //duration, in Seconds.
    private fun getVoiceDuration(data: ByteArray): Int {
        val frames = data.size / 30 //帧数
        val ms = frames * 20 //每帧20ms
        return (ms / 1000).clamp(1.0).toInt()
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemId(position: Int): Long {
        val message = getMessageByPosition(position)
        return message.hashCode().toLong()
    }

    fun appendData(messages: MutableList<Any>) {
        val oldSize = itemCount
        this.messages.addAll(messages)
        createTimeStampAnchor()
        notifyItemRangeInserted(oldSize, messages.size)
    }

    fun resetData(messages: MutableList<Any>) {
        this.messages.clear()
        this.messages.addAll(messages)
        createTimeStampAnchor()
        notifyDataSetChanged()
        Log.e("LAST-ITEM",this.messages.first().toString())
    }

    fun prependData(messages: MutableList<Any>) {
        this.messages.addAll(0, messages)
        createTimeStampAnchor()
        notifyItemRangeInserted(0, messages.size)
    }

    fun notifyPlaybackStart(currentPlayPosition: Int) {
        this.currentPlayPosition = currentPlayPosition
        println("currentPlayPosition = ${currentPlayPosition}")
        notifyItemChanged(currentPlayPosition)
    }

    fun notifyPlaybackStop() {
        notifyItemChanged(currentPlayPosition)
        this.currentPlayPosition = -1
    }

    fun getCurrentPlayPosition(): Int {
        return currentPlayPosition
    }

    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTime: TextView? = itemView.findViewById(R.id.textViewTime)
        val imageViewPlay: ImageView? = itemView.findViewById(R.id.imageViewPlay)
        val imageViewAvatar: ImageView? = itemView.findViewById(R.id.imageViewGroupIcon)
        val imageViewFile: FileDownloadImageView? = itemView.findViewById(R.id.imageViewFile)
        val textViewUserName: TextView? = itemView.findViewById(R.id.textViewUserName)
        val textViewDuration: TextView? = itemView.findViewById(R.id.textViewDuration)
        val layoutMessageBody: LinearLayout? = itemView.findViewById(R.id.layoutMessageBody)
        val textViewContent: TextView? = itemView.findViewById(R.id.textViewContent)
    }
}

