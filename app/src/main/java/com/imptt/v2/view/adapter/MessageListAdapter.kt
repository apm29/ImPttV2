package com.imptt.v2.view.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.imptt.v2.R
import com.imptt.v2.data.model.message.Message
import com.imptt.v2.data.model.message.MessageType
import java.lang.IllegalArgumentException
import java.text.SimpleDateFormat
import java.util.*

/**
 *  author : apm29[ciih]
 *  date : 2020/10/10 10:17 AM
 *  description :
 */
class MessageListAdapter constructor(
    private var messages: ArrayList<Message>,
    private val layoutInflater: LayoutInflater,
    private val onMessageClicked:((Message, View)->Unit)? = null,
    private val onUserClicked:((Message, View)->Unit)? = null
) : RecyclerView.Adapter<MessageListAdapter.VH>() {
    companion object{
        const val TYPE_FOOTER = 199999
    }

    override fun getItemViewType(position: Int): Int {
        if(position == 0){
            return TYPE_FOOTER
        }
        //倒序取消息
        //映射关系:
        //     MSG1 MSG2 MSG3 MSG4 FOOTER
        //     4    3    2    1    0
        return  messages[messages.size-position].contentType.type
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
        val view: View = when(viewType){
            MessageType.AUDIO_ME.type->{
                layoutInflater.inflate(R.layout.message_audio_layout,parent,false)
            }
            MessageType.AUDIO_OTHER.type->{
                layoutInflater.inflate(R.layout.message_audio_reverse_layout,parent,false)
            }
            MessageType.TEXT_ME.type->{
                layoutInflater.inflate(R.layout.message_text_layout,parent,false)
            }
            MessageType.TEXT_OTHER.type->{
                layoutInflater.inflate(R.layout.message_text_reverse_layout,parent,false)
            }
            TYPE_FOOTER->{
                layoutInflater.inflate(R.layout.message_footer_layout,parent,false)
            }
            else->{
                throw IllegalArgumentException("未知的消息类型:$viewType")
            }
        }

        return VH(view)
    }

    override fun onBindViewHolder(holder: VH, position: Int) {
        val contentType = getItemViewType(position)
        if(contentType == TYPE_FOOTER){
            return
        }
        holder.textViewUserName?.text = "用户${Random().nextInt(99)}"
        holder.textViewTime?.text = "${SimpleDateFormat().format(Date())}"
        holder.imageViewAvatar?.setOnClickListener {
            onUserClicked?.invoke(messages[position],it)
        }

        if(contentType == MessageType.AUDIO_OTHER.type || contentType == MessageType.AUDIO_ME.type) {
            //语音
            holder.textViewDuration?.text = "12s"
            holder.layoutMessageBody?.setOnClickListener {
                onMessageClicked?.invoke(messages[position], it)
            }
        } else if(contentType == MessageType.TEXT_OTHER.type || contentType == MessageType.TEXT_ME.type) {
            //文本
            holder.textViewContent?.text = "XXXXXX"
        }
    }


    override fun getItemCount(): Int {
        return messages.size + 1
    }

    fun newList(messages: java.util.ArrayList<Message>) {
        this.messages = messages
        notifyDataSetChanged()
    }
    class VH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val textViewTime:TextView? = itemView.findViewById(R.id.textViewTime)
        val imageViewAvatar:ImageView? = itemView.findViewById(R.id.imageViewGroupIcon)
        val textViewUserName:TextView? = itemView.findViewById(R.id.textViewUserName)
        val textViewDuration:TextView? = itemView.findViewById(R.id.textViewDuration)
        val layoutMessageBody:LinearLayout? = itemView.findViewById(R.id.layoutMessageBody)
        val textViewContent:TextView? = itemView.findViewById(R.id.textViewContent)
    }
}

