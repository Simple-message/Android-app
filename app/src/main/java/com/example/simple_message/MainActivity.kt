package com.example.simple_message

import SocketHandler
import android.Manifest
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Bundle
import android.util.Base64
import android.util.Base64.DEFAULT
import android.util.Base64.encodeToString
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.example.simple_message.R
import io.socket.engineio.parser.Base64.encodeToString
import org.json.JSONObject
import org.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*

class MainActivity : AppCompatActivity() {
    var socket: io.socket.client.Socket? = null
    var avatarBase64: String = ""
    var loginArea: EditText? = null
    var buttonLogIn: Button? = null
    var buttonRegister: Button? = null
    var uploadAvatar: ImageButton? = null
    var submit: Button? = null
    var isRegister: Boolean = false
    
    override fun onCreate(savedInstanceState : Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        SocketHandler.setSocket()
        socket = SocketHandler.getSocket()
        socket?.connect()

        val context = this
        val path = context.applicationInfo.dataDir
        val file = File("$path/tag.txt")
        val isNoFile = file.createNewFile()
        if (!isNoFile) {
            val name = file.readText()
            sendLoginEvent(name)
        }

        //region VIEWS
        buttonRegister = findViewById<Button>(R.id.buttonRegister)
        buttonLogIn = findViewById<Button>(R.id.buttonLogIn)
        loginArea = findViewById<EditText>(R.id.login)
        uploadAvatar = findViewById<ImageButton>(R.id.uploadAvatar)
        submit = findViewById(R.id.submit_button)
        //endregion

        uploadAvatar?.setOnClickListener {
            pickImage()
        }

        submit?.setOnClickListener {
            val loginName = loginArea?.text.toString()
            if (isRegister) {
                val loginData = "{\"login\":\"" + loginName + "\",\"avatar\":\"" + avatarBase64.replace("\n", "\\n") + "\"}"
                socket?.emit("register", loginData)
            } else {
                sendLoginEvent(loginName)
            }
        }

        buttonRegister?.setOnClickListener {
            showInputDataScreen(true)
        }

        buttonLogIn?.setOnClickListener {
            showInputDataScreen(false)
        }

        socket?.on("login") { args ->
            onLoginEvent(args)
        }
    }

    fun showInputDataScreen(isRegister: Boolean) {
        this.isRegister = isRegister
        loginArea?.visibility = View.VISIBLE
        buttonRegister?.visibility = View.GONE
        buttonLogIn?.visibility = View.GONE
        submit?.visibility = View.VISIBLE
        if (isRegister) {
            uploadAvatar?.visibility = View.VISIBLE
        }
    }

    fun hideInputDataScreen() {
        this.isRegister = false
        loginArea?.visibility = View.GONE
        buttonRegister?.visibility = View.VISIBLE
        buttonLogIn?.visibility = View.VISIBLE
        submit?.visibility = View.GONE
        uploadAvatar?.visibility = View.GONE
    }

    val singleImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                if (null != selectedImageUri) {
                    findViewById<ImageView>(R.id.uploadAvatar).setImageURI(selectedImageUri)
                    avatarBase64 = encode(selectedImageUri)
                }
            }
        }

    fun encode(imageUri: Uri): String {
        val input = getContentResolver().openInputStream(imageUri)
        val image = BitmapFactory.decodeStream(input, null, null)
        val baos = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        val imageBytes = baos.toByteArray()
        val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        return imageString
    }

    fun pickImage() {
        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE), 1)
        } else {
            val intent = Intent()
            intent.type = "image/*"
            intent.action = Intent.ACTION_GET_CONTENT
            singleImageResultLauncher.launch(Intent.createChooser(intent, "Select Picture"))
        }
    }

    fun sendLoginEvent(name: String) {
        socket?.emit("login", name)
    }

    fun startFeedActivity(uid: String) {
        val intent = Intent(this, FeedActivity::class.java)
        intent.putExtra("uid", uid)
        startActivity(intent)
    }

    fun onLoginEvent(args: Array<Any>) {
        val data = args[0] as String
        val jsonObject = JSONTokener(data).nextValue() as JSONObject
        val code = jsonObject.getString("code")
        val uid = jsonObject.getString("uid").toIntOrNull()
        if (code == "200" && uid != null) {
            runOnUiThread {
                startFeedActivity(uid.toString())
            }
        } else {
            runOnUiThread {
                Toast.makeText(this, "No such login!", Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.submit_screen, menu)
        return true
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when (item.itemId) {
            R.id.back -> hideInputDataScreen()
        }
        return super.onOptionsItemSelected(item)
    }
}
