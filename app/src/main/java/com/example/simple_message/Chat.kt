package com.example.simple_message

import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_message.factories.Message
import com.example.simple_message.adapters.ChatMessagesAdapter
import org.json.JSONObject
import org.json.JSONTokener
import java.time.ZonedDateTime

class Chat : AppCompatActivity() {
    //region MESSAGEMANAGERS
    private var messageLayoutManager: RecyclerView.LayoutManager? = null
    private var messageAdapter: RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder>? = null
    lateinit var messageInput: EditText
    lateinit var chat_messages: RecyclerView
    //endregion

    //region MESSAGE
    lateinit var sendButton: ImageButton
    var messages: ArrayList<Message> = ArrayList<Message>()
    var recieverUid: Int = 0
    var recieverName: String = ""
    //endregion

    var socket: io.socket.client.Socket? = null

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat)

        //get extras
        recieverUid = intent.getStringExtra("uid")!!.toInt()
        recieverName = intent.getStringExtra("name")!!

        //handle actionbar
        val actionbar = supportActionBar
        if (actionbar != null) {
            actionbar!!.title = recieverName
            actionbar.setDisplayHomeAsUpEnabled(true)
        }
        //set elements
        messageInput = findViewById(R.id.messageInput)
        sendButton = findViewById(R.id.send)
        sendButton.setOnClickListener {
            sendMessage()
        }

        //handle recycler view
        messageLayoutManager = LinearLayoutManager(this)
        messageAdapter = ChatMessagesAdapter(messages)
        chat_messages = findViewById<RecyclerView>(R.id.chat_messages)
        chat_messages.layoutManager = messageLayoutManager
        chat_messages.adapter = messageAdapter

        //handle sockets
        socket = SocketHandler.getSocket()
        socket!!.connect()
        getHistory()
        socket!!.on("history") { args ->
            handleHistory(args)
        }
        socket!!.on("messageToChat") { args ->
            handleMessageToChat(args)
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun getHistory() {
        val params = "{\"reciever_id\":"+recieverUid+"}"
        socket?.emit("history", params)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage() {
        var messageText: String = messageInput.text.toString()
        messageInput.setText("")
        val params = "{\"reciever_id\":"+recieverUid+",\"text\":\""+messageText+"\"}"
        socket?.emit("messageToChat", params)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun addMessage(message: JSONObject) {
        val messageText = message.getString("message_text")
        val sendTimeString = message.getString("send_time")
        val messageSenderId = message.getString("sender_id").toInt()
        var messageName = recieverName
        if (messageSenderId != this.recieverUid) {
            messageName = "Me"
        }
        val sendTime = ZonedDateTime.parse(sendTimeString)
        val message = Message(messageText, messageName, sendTime)
        runOnUiThread {
            messages.add(message)
            chat_messages.scrollToPosition(messages.size - 1);
        }
//                    (messageAdapter as ChatMessagesAdapter).addMessage(message)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleHistory(args: Array<Any>) {
        if (args[0] != null) {
            val data = args[0] as JSONObject
            val code = data.getString("code")
            if (code != "200") return
            val chatsRes = data.getJSONArray("result")
            for (i in 0 until chatsRes.length()) {
                val chat = chatsRes.getJSONObject(i)
                addMessage(chat)
            }
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleMessageToChat(args: Array<Any>) {
        if (args[0] != null) {
            val data = args[0] as String
            val jsonObject = JSONTokener(data).nextValue() as JSONObject
            val code = jsonObject.getString("code")
            if (code != "200") return
            val insertedSuccess = jsonObject.getBoolean("success")
            if (insertedSuccess) {
                addMessage(jsonObject)
            }
        }
    }
}