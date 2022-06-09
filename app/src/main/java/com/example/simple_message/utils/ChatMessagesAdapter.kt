package com.example.simple_message.utils

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_message.R

class ChatMessagesAdapter: RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder>() {

    val messages = arrayOf(arrayOf("hello", "20.00"), arrayOf("Hi", "20.01"))

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ChatMessagesAdapter.ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    override fun onBindViewHolder(holder: ChatMessagesAdapter.ViewHolder, position: Int) {
        holder.messageText.text = messages[position][0]
        holder.messageTime.text = messages[position][1]
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