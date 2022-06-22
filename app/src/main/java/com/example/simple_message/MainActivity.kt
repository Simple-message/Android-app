package com.example.simple_message

import SocketHandler
import android.Manifest
import android.R.attr.bitmap
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
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import io.socket.engineio.parser.Base64.encodeToString
import org.json.JSONObject
import org.json.JSONTokener
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class MainActivity : AppCompatActivity() {
    var socket: io.socket.client.Socket? = null
    var avatarBase64: String = ""
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
            //sendLoginEvent(name)
        }

        //region VIEWS
        val buttonRegister = findViewById<Button>(R.id.buttonRegister)
        val buttonLogIn = findViewById<Button>(R.id.buttonLogIn)
        val loginArea = findViewById<EditText>(R.id.login)
        val uploadAvatar = findViewById<ImageButton>(R.id.uploadAvatar)
        //endregion

        uploadAvatar.setOnClickListener{
            pickImage()
        }

        buttonRegister.setOnClickListener {
            val loginName = loginArea.text.toString()
            val loginData = "{\"login\":\""+loginName+"\",\"avatar\":\""+avatarBase64.replace("\n", "\\n")+"\"}"
            socket?.emit("register", loginData)
        }

        buttonLogIn.setOnClickListener {
            val loginName = loginArea.text.toString()
            sendLoginEvent(loginName)
        }

        socket?.on("login") { args ->
            onLoginEvent(args)
        }
    }

    val singleImageResultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                // There are no request codes
                val data: Intent? = result.data
                val selectedImageUri: Uri? = data?.data
                if (null != selectedImageUri) {
//                    val path = getPathFromURI(selectedImageUri)
//                    findViewById<TextView>(R.id.textView).text = path
                    findViewById<ImageView>(R.id.uploadAvatar).setImageURI(selectedImageUri)

                    avatarBase64 = encode(selectedImageUri)
                }
            }
        }

    fun encode(imageUri: Uri): String {
        val input = getContentResolver().openInputStream(imageUri)
        val image = BitmapFactory.decodeStream(input , null, null)

        // Encode image to base64 string
        val baos = ByteArrayOutputStream()
        image?.compress(Bitmap.CompressFormat.JPEG, 100, baos)
        var imageBytes = baos.toByteArray()
        val imageString = Base64.encodeToString(imageBytes, Base64.DEFAULT)
        return imageString
    }


    fun decode(imageString: String): Bitmap? {

        // Decode base64 string to image
        val imageBytes = Base64.decode(imageString, Base64.DEFAULT)
        val decodedImage = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        return decodedImage
    }

    fun pickImage() {
        if(ContextCompat.checkSelfPermission(this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {
            //No Permission
            ActivityCompat.requestPermissions(this, arrayOf(Manifest.permission.READ_EXTERNAL_STORAGE),1)
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
                runOnUiThread {
                    Toast.makeText(this, "No such login!", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }
}