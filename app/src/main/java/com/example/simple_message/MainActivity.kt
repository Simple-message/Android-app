package com.example.simple_message

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        //region BUTTONS
        val buttonToAChat = findViewById<Button>(R.id.button_to_a_chat)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonLogIn = findViewById<Button>(R.id.buttonLogIn)
        //endregion

        buttonToAChat.setOnClickListener {
            val intent = Intent(this, Chat::class.java)
            startActivity(intent)
        }

        buttonRegister.setOnClickListener {
            // here we need to connect to server and:
            // 1) Check if tag is used
            // 2) if true: send negative response
            //    if false: Add new person to db and redirect him to feedActivity
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
        }

        buttonLogIn.setOnClickListener {
            // here we need to connect to server and:
            // 1) Check if tag is used
            // 2) if true: check if password is correct:
            //             3) if true: Redirect person to feedActivity
            //                if false: send negative response
            //    if false: send negative response
            val intent = Intent(this, FeedActivity::class.java)
            startActivity(intent)
        }

    }
}