package com.example.simple_message

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_message.factories.Message
import com.example.simple_message.utils.ChatMessagesAdapter
import org.json.JSONObject
import org.json.JSONTokener
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.ZonedDateTime
import java.time.format.DateTimeFormatter

class Chat : AppCompatActivity() {
    //region MESSAGEMANAGERS
    private var messageLayoutManager: RecyclerView.LayoutManager? = null
    private var messageAdapter: RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder>? = null
    lateinit var messageInput: EditText
    //endregion
    //region MESSAGE
    lateinit var sendButton: ImageButton
    var messages: ArrayList<Message> = ArrayList<Message>()
    var selfUid:Int = 0
    var recieverUid: Int = 0
    //endregion
    var socket: io.socket.client.Socket? = null
    @RequiresApi(Build.VERSION_CODES.O)
//    val dateFormatter = DateTimeFormatter.ofPattern("YYYY-MM-DD'T'hh:mm:ss.SSSz");

//    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)
        val tag=intent.getStringExtra("tag")


        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.send)

        messageLayoutManager = LinearLayoutManager(this)
        messageAdapter = ChatMessagesAdapter(messages)

        var chat_messages = findViewById<RecyclerView>(R.id.chat_messages)
        chat_messages.layoutManager = messageLayoutManager
        chat_messages.adapter = messageAdapter

        sendButton.setOnClickListener {
            sendMessage()
        }

        // get params
        selfUid = 1
        recieverUid = 8

        SocketHandler.setSocket()
        socket = SocketHandler.getSocket()
        socket!!.connect()
        getHistory()
        socket!!.on("history") { args ->
            if (args[0] != null) {
                val data = args[0] as JSONObject
                val code = data.getString("code")
                val chatsRes = data.getJSONArray("result")
                val messageHistoryLength = chatsRes.length()
                for (i in 0 until chatsRes.length()) {
                    val chat = chatsRes.getJSONObject(i)
                    val messageText = chat.getString("message_text")
                    val sendTimeString = chat.getString("send_time")
                    val sendTime = ZonedDateTime.parse(sendTimeString)
                    messages.add(Message(messageText, sendTime))
                }
                (messageAdapter as ChatMessagesAdapter).notifyItemInserted(messageHistoryLength)
            }
        }

        socket!!.on("messageToChat") { args ->
            if (args[0] != null) {
                val data = args[0] as String
                val jsonObject = JSONTokener(data).nextValue() as JSONObject
                val code = jsonObject.getString("code")
                // handle code
                val chatsRes = jsonObject.getJSONArray("chats")
                for (i in 0 until chatsRes.length()) {
                    val chat = chatsRes.getJSONObject(i)
                    val messageText = chat.getString("message_text")
                    Log.d("Tag", messageText)
                    attach(chats, messageText)
                }
            }
        }

    }

    fun getHistory() {
        val params = "{\"sender_id\":"+selfUid+",\"reciever_id\":"+recieverUid+"}"
        socket?.emit("history", params)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage() {
        val messageHistoryLength = messages.size
        var messageText: String = messageInput.text.toString()
        val current = ZonedDateTime.now()

        messages.add(Message(messageText, current))

        // get string time for request to server
//        val formatter = DateTimeFormatter.ofPattern("HH:mm")
//        val messageTime = current.format(formatter)
        //somewhere here send post request to server
        socket?.emit("messageToChat", messageText)

        (messageAdapter as ChatMessagesAdapter).notifyItemInserted(messageHistoryLength)
    }
}