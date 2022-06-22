package com.example.simple_message

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Button
import android.widget.EditText
import org.json.JSONObject
import org.json.JSONTokener
import java.io.File

class MainActivity : AppCompatActivity() {
    var socket: io.socket.client.Socket? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SocketHandler.setSocket()
        socket = SocketHandler.getSocket()
        socket?.connect()

        val context = this
        val path = context.applicationInfo.dataDir
        val file = File("$path/tag.txt")
        val isNoFile = file.createNewFile()
        if(!isNoFile){
            var name = file.readText()
            name = "Bender"
            sendLoginEvent(name)
        }

        //region VIEWS
        val editTextTag = findViewById<EditText>(R.id.editTextTag)
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonLogIn = findViewById<Button>(R.id.buttonLogIn)
        val loginArea = findViewById<EditText>(R.id.login)
        //endregion

//        buttonRegister.setOnClickListener {
//            // here we need to connect to server and:
//            // 1) Check if tag is used
//            // 2) if true: send negative response
//            //    if false: Add new person to db and redirect him to feedActivity
//            val intent = Intent(this, FeedActivity::class.java)
//            val userTag = editTextTag.text
//            intent.putExtra("userTag",userTag)
//            startActivity(intent)
//        }

        buttonLogIn.setOnClickListener {
            val loginName = loginArea.text.toString()
            sendLoginEvent(loginName)
        }

        socket?.on("login") { args ->
            onLoginEvent(args)
        }

    }

    fun sendLoginEvent(name: String) {
        socket?.emit("login", name)
    }

    fun startFeedActivity(uid: String) {
        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtra("uid",uid)
        startActivity(intent)
    }

    fun onLoginEvent(args: Array<Any>) {
        if (args[0] != null) {
            val data = args[0] as String
            val jsonObject = JSONTokener(data).nextValue() as JSONObject
            val code = jsonObject.getString("code")
            val uid = jsonObject.getString("uid").toIntOrNull()
            if (code == "200" && uid != null) {
                runOnUiThread {
                    startFeedActivity(uid.toString())
                }
            } else {
                // show error
            }
        }
    }
}