package com.example.simple_message

import android.content.Intent
import android.graphics.BitmapFactory
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_message.adapters.feedAdapter
import com.example.simple_message.factories.Feed
import org.json.JSONObject
import org.json.JSONTokener
import java.util.concurrent.Executors

class FeedActivity : AppCompatActivity() {

    var uid: String? = null
    var socket: io.socket.client.Socket? = null
    var chats = arrayOf<String?>()
    lateinit var feed: Feed

        override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        uid = intent.getStringExtra("uid")

        feed = Feed(initialgetTag(), chats)
        //region VIEWS
        val feedView = findViewById<RecyclerView>(R.id.chatList)
        val buttonNewChat = findViewById<Button>(R.id.buttonNewChat)
        //endregion

        // here we need a request to server getAllChats (tag)
        feedView.layoutManager = LinearLayoutManager(this)
        var adapter = feedAdapter(feed.chats)
        feedView.adapter = adapter
        adapter.setOnItemClickListener(object: feedAdapter.OnItemClickListener{
            override fun OnClick(position: Int) {
                // val name = feed.chats[position].toString()
                openChat("mila", "19") // put here name and reciever id
            }
        })

        buttonNewChat.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            builder.setTitle("Змініть параметри")
            val dialogLayout = inflater.inflate(R.layout.new_chat, null)
            val tag = dialogLayout.findViewById<EditText>(R.id.tagNewChat).text.toString()
            builder.setView(dialogLayout)
            builder.setPositiveButton("OK"){ _, _ ->
                feed.chats = attach(feed.chats, tag)
                feedView.adapter = feedAdapter(feed.chats)
            }
            builder.setNegativeButton("Cancel"){ _, _ ->
            }
            builder.show()
            //show some pop-up asking for tag
        }
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
                    // val name = chat.getString("name")
                    feed.chats = attach(feed.chats, messageText)
                }
                adapter.updateTags(feed.chats)
            }
        }
    }

    fun loadAvatar(uid: Int, iv: ImageView) {
        val executor = Executors.newSingleThreadExecutor()
        val handler = Handler(Looper.getMainLooper())

        executor.execute {
            val imageURL = "http://192.168.10.105:8000/fileServer/avatars/"+uid+".png"
            try {
                val `in` = java.net.URL(imageURL).openStream()
                val bitmap = BitmapFactory.decodeStream(`in`)
                handler.post {
                    iv.setImageBitmap(bitmap)
                }
            }
            catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    fun initialgetTag(): String {
        return "";
    }

    fun attach (arr: Array<String?>, str: String): Array<String?> {
        val array = arr.copyOf(arr.size + 1)
        array[arr.size] = str
        return array
    }

    fun openChat(name: String, uid: String){
        val intent = Intent(this, Chat::class.java)
        intent.putExtra("uid",uid)
        intent.putExtra("name",name)
        startActivity(intent)
    }
}
