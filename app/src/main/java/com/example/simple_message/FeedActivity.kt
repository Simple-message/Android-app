package com.example.simple_message

import android.app.AlertDialog
import android.content.Intent
import android.graphics.BitmapFactory
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_message.adapters.feedAdapter
import com.example.simple_message.factories.Chat
import com.example.simple_message.factories.Feed
import com.example.simple_message.R
import org.json.JSONObject
import org.json.JSONTokener
import java.time.ZonedDateTime
import java.util.concurrent.Executors

class FeedActivity : AppCompatActivity() {

    var uid: String? = null
    var socket: io.socket.client.Socket? = null
    var chats = arrayOf<Chat?>()
    lateinit var feed: Feed
    lateinit var builder: AlertDialog.Builder
    lateinit var dialogLayout: RecyclerView.ViewHolder

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        uid = intent.getStringExtra("uid")

        feed = Feed(chats)
        val feedView = findViewById<RecyclerView>(R.id.chatList)

        // here we need a request to server getAllChats (tag)
        feedView.layoutManager = LinearLayoutManager(this)
        val adapter = feedAdapter(feed.chats)
        feedView.adapter = adapter
        adapter.setOnItemClickListener(object : feedAdapter.OnItemClickListener {
            override fun OnClick(position: Int) {
                val name = feed.chats[position]?.name.toString()
                val uid = feed.chats[position]?.uid.toString()
                openChat(name, uid)
            }
        })

        socket = SocketHandler.getSocket()
        socket!!.connect()
        socket?.emit("getChats")
        socket!!.on("chats") { args ->
            if (args[0] != null) {
                val data = args[0] as String
                val jsonObject = JSONTokener(data).nextValue() as JSONObject
                val code = jsonObject.getString("code")
                // handle code
                if (code != "200") return@on
                val chatsRes = jsonObject.getJSONArray("result")
                for (i in 0 until chatsRes.length()) {
                    val chat = chatsRes.getJSONObject(i)
                    val messageText = chat.getString("message_text")
                    val name = chat.getString("name")
                    val uid = chat.getString("reciever_id")
                    val sendTimeString = chat.getString("send_time")
                    val sendTime = ZonedDateTime.parse(sendTimeString)
                    val message = Chat(uid, messageText, name, sendTime)
                    feed.chats = attach(feed.chats, message)
                }
                adapter.updateTags(feed.chats)
            }
        }

        socket!!.on("uid") { args ->
            if (args[0] != null) {
                val data = args[0] as String
                val jsonObject = JSONTokener(data).nextValue() as JSONObject
                val code = jsonObject.getString("code")
                // handle code
                if (code != "200") {
                    runOnUiThread {
                        Toast.makeText(this, "No such name!", Toast.LENGTH_SHORT).show()
                    }
                    return@on
                }
                val uid = jsonObject.getString("uid")
                val name = jsonObject.getString("name")
                val chat = Chat(uid, "", name, null)
                builder.setPositiveButton("OK") { _, _ ->
                    feed.chats = attach(feed.chats, chat)
                    feedView.adapter = feedAdapter(feed.chats)
                }
                builder.setNegativeButton("Cancel") { _, _ ->
                }
                builder.show()
            }
        }
    }

    fun loadAvatar(uid: Int, iv: ImageView) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val imageURL = "http://10.0.2.2:8000/fileServer/avatars/" + uid + ".png"
            try {
                val `in` = java.net.URL(imageURL).openStream()
                val bitmap = BitmapFactory.decodeStream(`in`)
                handler.post {
                    iv.setImageBitmap(bitmap)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun attach(arr: Array<Chat?>, str: Chat) : Array<Chat?> {
        val array = arr.copyOf(arr.size + 1)
        array[arr.size] = str
        return array
    }

    fun openChat(name: String, uid: String) {
        val intent = Intent(this, ChatActivity::class.java)
        intent.putExtra("uid", uid)
        intent.putExtra("name", name)
        startActivity(intent)
    }
}