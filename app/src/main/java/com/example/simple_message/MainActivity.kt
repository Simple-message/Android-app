package com.example.simple_message

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val buttonToAChat = findViewById<Button>(R.id.button_to_a_chat)
        buttonToAChat.setOnClickListener {
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }
    }
}