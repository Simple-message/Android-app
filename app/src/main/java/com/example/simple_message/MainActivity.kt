package com.example.simple_message

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SocketHandler.setSocket()
        val socket = SocketHandler.getSocket()
        socket.connect()
        val context = this
        val path = context.applicationInfo.dataDir
        val file = File("$path/tag.txt")
        val isNoFile = file.createNewFile()
        if(!isNoFile){
            val userTag = file.readText()
            val intent = Intent(this, FeedActivity::class.java)
            intent.putExtra("userTag",userTag)
            startActivity(intent)
        }

        //region VIEWS
        val editTextTag = findViewById<EditText>(R.id.editTextTag)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonLogIn = findViewById<Button>(R.id.buttonLogIn)
        //endregion
        val loginArea = findViewById<EditText>(R.id.login)

        buttonRegister.setOnClickListener {
            // here we need to connect to server and:
            // 1) Check if tag is used
            // 2) if true: send negative response
            //    if false: Add new person to db and redirect him to feedActivity
            val intent = Intent(this, FeedActivity::class.java)
            val userTag = editTextTag.text
            intent.putExtra("userTag",userTag)
            startActivity(intent)
        }

        buttonLogIn.setOnClickListener {
            socket.emit("login", loginArea.text)
            socket.on("login") { args ->
                if (args[0] != null)
                {
                    val data = args[0] as String
                    val jsonObject = JSONTokener(data).nextValue() as JSONObject
                    val code = jsonObject.getString("code")
                    val uid = jsonObject.getString("uid").toIntOrNull()
                    if (code == "200" && uid != null) {
                        runOnUiThread {
                            val intent = Intent(this, FeedActivity::class.java)
                            intent.putExtra("uid",uid.toString())
                            startActivity(intent)
                        }
                    } else {
                        // show error
                    }
                }
            }
            // here we need to connect to server and:
            // 1) Check if tag is used
            // 2) if true: check if password is correct:
            //             3) if true: Redirect person to feedActivity
            //                if false: send negative response
            //    if false: send negative response
//            val intent = Intent(this, FeedActivity::class.java)
//            val userTag = editTextTag.text
//            intent.putExtra("userTag",userTag)
//            startActivity(intent)
        }

    }
}