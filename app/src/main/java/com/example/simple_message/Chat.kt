package com.example.simple_message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Adapter
import android.widget.AdapterView
import android.widget.EditText
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_message.utils.ChatMessagesAdapter

class Chat : AppCompatActivity() {
    private var messageLayoutManager: RecyclerView.LayoutManager? = null
    private var messageAdapter: RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder>? = null
    lateinit var messageInput: EditText
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        messageInput = findViewById(R.id.messageInput)

        messageLayoutManager = LinearLayoutManager(this)
        var chat_messages = findViewById<RecyclerView>(R.id.chat_messages)
        chat_messages.layoutManager = messageLayoutManager
        messageAdapter = ChatMessagesAdapter()
        chat_messages.adapter = messageAdapter
    }
}