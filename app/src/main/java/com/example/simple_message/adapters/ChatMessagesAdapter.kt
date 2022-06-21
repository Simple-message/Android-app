package com.example.simple_message.adapters

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

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.chat_message, parent, false)
        val viewHolder = ViewHolder(view)
        return viewHolder
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val message = messages.get(position)
        holder.messageText.text = message.text
        holder.messageName.text = message.name
        val time = message.time
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
        var messageName: TextView

        init {
            messageText = messageView.findViewById(R.id.message_text)
            messageTime = messageView.findViewById(R.id.message_time)
            messageName = messageView.findViewById(R.id.message_name)
        }
    }

    fun addMessage(message: Message) {
        messages.add(message)
    }

}