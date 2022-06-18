package com.example.simple_message

import android.content.DialogInterface
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.simple_message.adapters.feedAdapter
import com.example.simple_message.factories.Feed

class FeedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)
        //here we get tag from file or server
        var feed = Feed(initialgetTag(), initialGetChats());
        //region VIEWS
        val feedView = findViewById<RecyclerView>(R.id.chatList)
        val buttonNewChat = findViewById<Button>(R.id.buttonNewChat)
        //endregion

        // here we need a request to server getAllChats (tag)
        feedView.layoutManager = LinearLayoutManager(this)
        feedView.adapter = feedAdapter(feed.chats)
        // display all chat

        buttonNewChat.setOnClickListener {
            val builder = AlertDialog.Builder(this)
            val inflater = layoutInflater
            builder.setTitle("Змініть параметри")
            val dialogLayout = inflater.inflate(R.layout.new_chat, null)
            val tag = dialogLayout.findViewById<EditText>(R.id.tagNewChat).text.toString()
            builder.setView(dialogLayout)
            builder.setPositiveButton("OK"){ dialogInterface: DialogInterface, i: Int ->
                feed.chats = attach(feed.chats, tag)
                feedView.adapter = feedAdapter(feed.chats)
            }
            builder.setNegativeButton("Cancel"){ dialogInterface: DialogInterface, i: Int ->
            }
            builder.show()
            //show some pop-up asking for tag
        }
    }

    fun initialgetTag(): String{
        return ""
        // go to file, check if tag exists
    }

    fun initialGetChats(): Array<String?> {
        // go to file
        val chats = arrayOf<String?>("kfsjlsd", "dlkfsl", "osfkd")
        //val chats : Array<String?> = emptyArray()
        return chats
    }

    fun attach (arr: Array<String?>, str: String): Array<String?> {
        val array = arr.copyOf(arr.size + 1)
        array[arr.size] = str
        return array
    }
}