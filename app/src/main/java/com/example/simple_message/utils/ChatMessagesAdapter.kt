package com.example.simple_message.utils

import android.os.Build
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_message.R
import com.example.simple_message.factories.Message
import java.time.format.DateTimeFormatter

class ChatMessagesAdapter(messages: ArrayList<Message>): RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder>() {

    private var messages: ArrayList<Message>

    init {
        this.messages = messages
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessagesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ChatMessagesAdapter.ViewHolder, position: Int) {
        holder.messageText.text = messages.get(position).text
        val time = messages.get(position).time
        val formatter = DateTimeFormatter.ofPattern("HH:mm")
        val messageTime = time.format(formatter)
        holder.messageTime.text = messageTime
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    inner class ViewHolder(messageView: View): RecyclerView.ViewHolder(messageView) {
        var messageText: TextView
        var messageTime: TextView

        init {
            messageText = messageView.findViewById(R.id.message_text)
            messageTime = messageView.findViewById(R.id.message_time)
        }
    }

}