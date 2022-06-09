package com.example.simple_message

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_message.factories.Message
import com.example.simple_message.utils.ChatMessagesAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

class Chat : AppCompatActivity() {
    private var messageLayoutManager: RecyclerView.LayoutManager? = null
    private var messageAdapter: RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder>? = null
    lateinit var messageInput: EditText
    lateinit var sendButton: ImageButton
    lateinit var messages: ArrayList<Message>

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.send)
        messages = ArrayList<Message>()
        //idk how to create
        val current = LocalDateTime.now()
        messages.add(Message("hello", current))
        messages.add(Message("Hi", current))

        messageLayoutManager = LinearLayoutManager(this)
        messageAdapter = ChatMessagesAdapter(messages)

        var chat_messages = findViewById<RecyclerView>(R.id.chat_messages)
        chat_messages.layoutManager = messageLayoutManager
        chat_messages.adapter = messageAdapter

        sendButton.setOnClickListener {
            sendMessage()
        }

    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage() {
        val messageHistoryLength = messages.size
        var messageText: String = messageInput.text.toString()
        val current = LocalDateTime.now()

        messages.add(Message(messageText, current))

        // get string time for request to server
//        val formatter = DateTimeFormatter.ofPattern("HH:mm")
//        val messageTime = current.format(formatter)
        //somewhere here send post request to server

        (messageAdapter as ChatMessagesAdapter).notifyItemInserted(messageHistoryLength)
    }
}