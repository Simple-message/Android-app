package com.example.simple_message

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class FeedActivity : AppCompatActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_feed)

        val buttonNewChat = findViewById<Button>(R.id.buttonNewChat)

        // here we need a request to server getAllChats (tag)
        // display all chat

        buttonNewChat.setOnClickListener {
            //show some pop-up asking for tag
        }
    }
}