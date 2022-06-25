package com.example.simple_message

import SocketHandler
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.Menu
import android.view.MenuItem
import android.widget.*
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_message.adapters.ChatMessagesAdapter
import com.example.simple_message.factories.Message
import com.example.simple_message.R
import org.json.JSONObject
import org.json.JSONTokener
import java.time.ZonedDateTime
import java.util.*
import java.util.concurrent.Executors

class ChatActivity() : AppCompatActivity() {
    //region MESSAGEMANAGERS
    private var messageLayoutManager: RecyclerView.LayoutManager? = null
    private var messageAdapter: RecyclerView.Adapter<ChatMessagesAdapter.ViewHolder>? = null
    lateinit var messageInput: EditText
    var menu: Menu? = null
    lateinit var chat_messages: RecyclerView
    var bitmap: Bitmap? = null
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
        setTitle("")

        val actionBar = getSupportActionBar()
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true)
        }

        //get extras
        recieverUid = intent.getStringExtra("uid")!!.toInt()
        recieverName = intent.getStringExtra("name")!!

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

        loadAvatar(recieverUid)
    }

    fun loadAvatar(uid: Int) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val imageURL = "http://10.0.2.2:8000/fileServer/avatars/" + uid + ".png"
            try {
                val `in` = java.net.URL(imageURL).openStream()
                bitmap = BitmapFactory.decodeStream(`in`)
                handler.post {
                    val avatarItem = menu?.findItem(R.id.menu_avatar)
                    val iv = ImageView(this@ChatActivity)
                    iv.maxHeight = 18
                    iv.maxWidth = 18
                    avatarItem?.setActionView(iv)
                    iv.setImageBitmap(bitmap)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    override fun onSupportNavigateUp(): Boolean {
        onBackPressed()
        return true
    }

    fun getHistory() {
        val params = "{\"reciever_id\":" + recieverUid + "}"
        socket?.emit("history", params)
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        this.menu = menu
        menuInflater.inflate(R.menu.chat_menu, menu)
        menu?.findItem(R.id.menu_name)?.setTitle(recieverName)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.menu_avatar -> Toast.makeText(this, "avatar clicked", Toast.LENGTH_SHORT).show()
        }
        return super.onOptionsItemSelected(item)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun sendMessage() {
        val messageText: String = messageInput.text.toString()
        messageInput.setText("")
        val params = "{\"reciever_id\":" + recieverUid + ",\"text\":\""+messageText+"\"}"
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
            chat_messages.scrollToPosition(messages.size - 1)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleHistory(args: Array<Any>) {
        val data = args[0] as JSONObject
        val code = data.getString("code")
        if (code != "200") return
        val chatsRes = data.getJSONArray("result")
        for (i in 0 until chatsRes.length()) {
            val chat = chatsRes.getJSONObject(i)
            addMessage(chat)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun handleMessageToChat(args: Array<Any>) {
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
